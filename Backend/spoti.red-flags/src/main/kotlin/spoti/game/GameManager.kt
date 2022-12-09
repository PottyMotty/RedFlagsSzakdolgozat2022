package spoti.game

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import spoti.config.PhaseGraphConfig
import spoti.config.TimeLimit
import spoti.config.defaultPhaseGraphConfig
import spoti.data.Player
import spoti.data.mapToPlayerState
import spoti.data.model.websocket.DateInfo
import spoti.data.model.websocket.LeaderBoardEntry
import spoti.data.model.websocket.PlayerState
import spoti.data.model.websocket.out.*
import spoti.exceptions.RedFlagsException

import spoti.other.Phase
import spoti.other.extensions.asJson
import spoti.other.extensions.isCraftingPhase
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class GameManager(
    val packs: List<Int>,
    val numRounds: Int,
    val room: Room,
    override val coroutineContext: CoroutineContext = Dispatchers.IO
) : CoroutineScope {
    companion object {
        const val UPDATE_TIME_FREQUENCY = 1000L
        const val WINNER_REWARD_HAPPY_PHASE = 50
        const val WINNER_REWARD_SABOTAGE_PHASE = 75
        const val BEST_SABOTAGE_REWARD = 25
        const val SUCCESSFULL_SABOTAGE_REWARD = 10
        const val MIN_PLAYERS_COUNT = 3
    }

    var dateOptions: List<DateInfo> = emptyList()
    var single: Player? = null
    var currentRoundNumber = 1
    val pastSinglesIDs = mutableListOf<String>()
    var isSabotageStage: Boolean = false
    var firstRoundWinner: DateInfo? = null
    var finalWinner: DateInfo? = null
    var sabotagePairing: Map<String, String> = mapOf()
    var leaderBoardStandings: List<LeaderBoardEntry> = emptyList()
    var doneWorking: MutableStateFlow<AtomicInteger> = MutableStateFlow(AtomicInteger(0))

    fun setupNextRound() {
        sabotagePairing = mapOf()
        dateOptions = emptyList()
        single = null
        isSabotageStage = false
        firstRoundWinner = null
        finalWinner = null
        doneWorking.update { AtomicInteger(0) }
    }


    private var phaseGraphConfig: PhaseGraphConfig = defaultPhaseGraphConfig(this)
    val countdownCurrentValue = MutableStateFlow<Long?>(null)
    private val errorFlow = MutableSharedFlow<RedFlagsException>(replay = 1)
    private var timerJob: Job? = null

    init {
        listenToPhaseChanges()
        listenToTime()
        handleErrors()
        listenToDoneWorking()
    }

    fun getCurrentPhase(): Phase {
        return phaseGraphConfig.currentPhaseConfig.value?.phase ?: throw Exception("No phase currently")
    }

    fun manualPhaseStep(phase: Phase) {
        phaseGraphConfig.setPhase(phase)
    }

    private fun listenToDoneWorking() {
        doneWorking.onEach { doneCount ->
            if (getCurrentPhase().isCraftingPhase()
                && doneCount.get() >= room.players.size.minus(1)
            ) {
                goNextPhase()
            }
            room.broadcast(DonePlayersCountChanged(doneCount.get()).asJson())
        }.launchIn(this)
    }

    fun createGameStateRemainderFor(playerClientID: String): GameStateReminder {
        val player = room.findPlayerByPredicate { it.clientId == playerClientID }
        val isRoomOwner = room.roomOwner?.clientId == playerClientID
        return GameStateReminder(
            players = room.mapPlayersListToPlayerState(),
            phase = getCurrentPhase(),
            personalData = PlayerState(
                username = player.username,
                imageURL = player.imageURL,
                roomOwner = isRoomOwner,
                isConnected = true
            ),
            singlePlayer = single?.mapToPlayerState(),
            remainingTime = countdownCurrentValue.value,
            cardsInHand = player.cardsInHand,
            lastSubmittedDate = dateOptions.find { it.createdBy == player.username },
            lastSabotagedDate = dateOptions.find { it.sabotagedBy == player.username },
            allDates = dateOptions,
            winnerDate = finalWinner ?: firstRoundWinner,
            dateToSabotage = dateOptions.find { it.createdBy == sabotagePairing[player.username] },
            leaderBoardStanding = leaderBoardStandings
        )
    }

    fun pause() {
        timerJob?.cancel()
    }

    fun resume() {
        countdownCurrentValue.value?.let { timeLeftOfPhase ->
            timeAndNotify(timeLeftOfPhase)
        }
    }


    private fun handleErrors() {
        errorFlow.onEach { exception ->
            when (exception) {
                else -> {
                    room.broadcast(ErrorAnnouncement("Error in game logic").asJson())
                    exception.printStackTrace()
                }
            }
        }.launchIn(this)
    }

    private fun listenToPhaseChanges() {
        phaseGraphConfig.currentPhaseConfig.map { it?.phase }.filterNotNull().onEach { phase ->
            phaseGraphConfig[phase].prepPhase?.let { it() }
            val phaseConfig = phaseGraphConfig[phase]
            val phaseDuration = when (val limit = phaseConfig.timeLimit) {
                TimeLimit.NoLimit -> null
                is TimeLimit.TimeLimitInMilliseconds -> {
                    limit.time
                }
            }
            val phaseChange = PhaseChange(phase, phaseDuration)
            room.broadcast(phaseChange.asJson())
            phaseGraphConfig[phase].phaseStrategy?.let { it(gameManager = this) }
            when (val timeLimit = phaseConfig.timeLimit) {
                is TimeLimit.TimeLimitInMilliseconds -> {
                    timeAndNotify(timeLimit.time)
                }

                else -> {
                    timerJob?.cancel()
                    countdownCurrentValue.value = null
                }
            }
        }.catch { error ->
            if (error is RedFlagsException) errorFlow.tryEmit(error)
        }.launchIn(this)
    }

    private fun listenToTime() {
        launch {
            countdownCurrentValue.filterNotNull().collect { timeLeft ->
                val timeLeftMsg = TimeLeftOfPhase(timeLeft = timeLeft, phase = getCurrentPhase())
                room.broadcast(timeLeftMsg.asJson())
            }
        }
    }

    private fun timeAndNotify(ms: Long) {
        timerJob?.cancel()
        timerJob = launch {
            repeat((ms / UPDATE_TIME_FREQUENCY).toInt()) {
                countdownCurrentValue.tryEmit(ms - (it * UPDATE_TIME_FREQUENCY))
                delay(UPDATE_TIME_FREQUENCY)
            }
            goNextPhase()
        }
    }

    fun fillUpDateOptionsPreSabotage() {
        room.players.filter { it.clientId != single?.clientId }.forEach { player ->
            if (dateOptions.find { info -> info.createdBy == player.username } == null)
                dateOptions += DateInfo(positiveAttributes = listOf(), createdBy = player.username)
        }
    }

    fun fillUpDateOptionsAfterSabotage() {
        sabotagePairing.let { pairs ->
            val switched: Map<String, String> = pairs.entries.associate { (k, v) -> v to k }
            dateOptions.filter { it.sabotagedBy == null }.forEach { unfinishedDate ->
                val sabotager = switched[room.findPlayer(unfinishedDate.createdBy).username]
                val finishedDate = unfinishedDate.copy(sabotagedBy = sabotager)
                dateOptions -= unfinishedDate
                dateOptions += finishedDate
            }
        }
    }


    private fun goNextPhase() {
        phaseGraphConfig.nextPhase()
    }

    fun handOutPoints() {
        val happyPhaseWinner = room.findPlayer(firstRoundWinner?.createdBy)
        happyPhaseWinner.pointsThisRound += WINNER_REWARD_HAPPY_PHASE

        val sabotagePhaseWinner = room.findPlayer(finalWinner?.createdBy)
        sabotagePhaseWinner.pointsThisRound += WINNER_REWARD_SABOTAGE_PHASE
        val bestSabotagerUsername =
            dateOptions.first { it.createdBy == happyPhaseWinner.username }.sabotagedBy
        if (happyPhaseWinner.clientId != sabotagePhaseWinner.clientId) {
            val bestSabotager = room.findPlayer(bestSabotagerUsername)
            bestSabotager.pointsThisRound += BEST_SABOTAGE_REWARD
        }
        room.players.filter { it.username != finalWinner?.sabotagedBy }
            .forEach { player -> player.pointsThisRound += SUCCESSFULL_SABOTAGE_REWARD }
    }

    fun startGame() {
        if (getCurrentPhase() == Phase.PLAYERS_GATHERED)
            goNextPhase()
    }

    fun winnerSelected(winnerDateOption: DateInfo) {
        if (isSabotageStage) {
            finalWinner = winnerDateOption
        } else {
            firstRoundWinner = winnerDateOption
        }
        goNextPhase()
    }
}


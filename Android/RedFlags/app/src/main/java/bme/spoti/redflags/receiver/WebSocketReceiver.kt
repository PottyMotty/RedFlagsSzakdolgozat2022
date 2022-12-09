package bme.spoti.redflags.receiver

import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.data.model.websocket.`in`.*
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class WebSocketReceiver(
	private val redFlagsSocketService: RedFlagsSocketService,
	private val gameRepository: GameRepository,
	override val coroutineContext: CoroutineContext = Dispatchers.IO
) : CoroutineScope {
	var messageObserverJob: Job? = null

	fun stopListening() {
		messageObserverJob?.cancel()
	}
	//TODO make it more OOP
	fun listenToMessages() {
		messageObserverJob = launch {
			redFlagsSocketService.observeMessages().cancellable().collect { message ->
				Timber.d("Message handled in receiver")
				gameRepository.apply {
					when (message) {
						is RoomState -> {
							playersInRoom.update { it.plus(message.players) }
							personalInfo.update { message.players.last() }
						}
						is PlayerJoinedRoom -> {
							playersInRoom.update {
								it.plus(
									message.joinedPlayer
								)
							}
						}
						is PlayerLeftRoom -> {
							playersInRoom.update { list->
								val toRemove = list.first { it.username == message.leftUsername }
								val newRoomOwner = list.first { it.username == message.newRoomOwner }
								val newList = list.minus(
									listOf(toRemove, newRoomOwner).toSet()
								).plus(newRoomOwner.copy(roomOwner = true))
								newList
							}
							if(message.newRoomOwner == personalInfo.value?.username){
								personalInfo.update { personalInfo -> personalInfo?.copy(roomOwner = true) }
							}
						}
						is SingleAnnouncement -> {
							singlePlayer.update { message.singlePlayer }
						}
						is ErrorAnnouncement -> {
							errorFlow.tryEmit(message.message)
						}
						is TimeLeftOfPhase -> {
							Timber.d("Time left of phase arrived")
							phaseRemainingTime.value =  message.timeLeft
						}
						is PhaseChange -> {
							phase.tryEmit(message.phase)
							phaseDuration.update { message.phaseDuration }
						}
						is AllDates -> {
							allDates.update { message.dates }
						}
						is DateToSabotage -> {
							dateToSabotage.update { message.dateInfoToSabotage }
						}
						is DealtCards -> {
							cardsInHand.update { message.cards }
						}
						is DonePlayersCountChanged -> {
							doneCount.update { message.count }
						}
						is LeaderBoardData -> {
							leaderBoardStandings.update { message.leaderBoard }
						}
						is PlayerConnectionStateChanged ->{
							playersInRoom.update { message.players }
						}
						is WinnerAnnouncement ->{
							winnerDate.update { message.winnerDateOption }
						}
						is GameStateReminder -> {
							phase.update { message.phase }
							playersInRoom.update { message.players }
							personalInfo.update { message.personalData }
							singlePlayer.update { message.singlePlayer }
							phaseRemainingTime.update { message.remainingTime }
							cardsInHand.update { message.cardsInHand }
							lastSubmittedDate.update { message.lastSubmittedDate }
							lastSabotagedDate.update { message.lastSabotagedDate }
							allDates.update { message.allDates }
							winnerDate.update { message.winnerDate }
							dateToSabotage.update { message.dateToSabotage }
							leaderBoardStandings.update { message.leaderBoardStanding }
						}
						else -> {
							throw Exception("Unhandled pakcage")
						}
					}
				}
			}
		}
	}
}

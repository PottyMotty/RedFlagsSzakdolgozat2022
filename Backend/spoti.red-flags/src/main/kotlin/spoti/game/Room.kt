package spoti.game

import com.google.gson.Gson
import io.ktor.websocket.*
import kotlinx.coroutines.*
import spoti.data.Player
import spoti.data.mapToPlayerState

import spoti.data.model.websocket.PlayerState
import spoti.data.model.websocket.out.PlayerConnectionStateChanged
import spoti.data.model.websocket.out.PlayerJoinedRoom
import spoti.data.model.websocket.out.PlayerLeftRoom
import spoti.data.model.websocket.out.RoomState
import spoti.exceptions.PlayerNotFound
import spoti.extensions.sendError
import spoti.other.Phase
import spoti.other.extensions.asJson
import kotlin.coroutines.CoroutineContext

class Room(
    var players: List<Player> = emptyList(),
    private val password: String,
    override val coroutineContext: CoroutineContext = Dispatchers.IO,
) : CoroutineScope{
    val roomOwner: Player? get() = players.find { it.isRoomOwner }

    lateinit var game : GameManager

    private var disconnectedClients: List<String> = emptyList()
    var shouldBeDeleted = false
    suspend fun broadcast(msg: String, exclude: List<Player> = listOf()) {
        players.filter { player -> !exclude.contains(player) }.forEach { player ->
            if (player.socket.isActive && player.isConnected) {
                player.socket.send(Frame.Text(msg))
            }
        }
    }
    val isPasswordProtected get()= password.isNotEmpty()
    val playerCount: Int get() = players.size
    fun getRandomPlayer(exclusionList: List<String>): Player {
        var candidate = players.random()
        while (exclusionList.contains(candidate.clientId)) {
            candidate = players.random()
        }
        return candidate
    }
    fun createGame(packs: List<Int>, numRounds: Int) {
        game  = GameManager(
            packs = packs,
            numRounds = numRounds,
            room = this
        )
    }
    fun reset() {
        players.forEach { it.reset() }
    }

    fun containsPlayer(username: String): Boolean {
        return players.any { it.username == username }
    }

    private fun containsClient(clientId: String): Boolean {
        return players.any { it.clientId == clientId }
    }
    fun handleConnectionLost(clientID: String){
        disconnectedClients = disconnectedClients.plus(clientID)
        val lostPlayer = findPlayerByPredicate { it.clientId == clientID }
        lostPlayer.isConnected =false
        game.pause()
        launch {
            broadcast(
                PlayerConnectionStateChanged(
                    players.map {
                        it.mapToPlayerState()
                    }
                ).asJson())
        }
        if(disconnectedClients.size == playerCount){
            println("Should be deleted set to true because everyone is disconnected")
            shouldBeDeleted = true
        }
    }
    fun isClientDisconnected(clientID: String): Boolean{
        return disconnectedClients.contains(clientID)
    }
    fun addPlayerToGame(player: Player, roomPassword: String?){
        launch {
            val successfullyAdded = addPlayer(player, roomPassword)
            if(successfullyAdded) {
                println("Should be deleted set to false")
                shouldBeDeleted = false
                if (isClientDisconnected(player.clientId)) {
                    disconnectedClients = disconnectedClients.minus(player.clientId)
                    broadcast(
                        PlayerConnectionStateChanged(
                            players.map {
                                it.mapToPlayerState()
                            }
                        ).asJson())
                    if(disconnectedClients.isEmpty()){
                        game.resume()
                    }
                    val reconnectedPlayer = findPlayerByPredicate { it.clientId == player.clientId }
                    reconnectedPlayer.socket.send(game.createGameStateRemainderFor(player.clientId).asJson())
                }
                if (playerCount >= GameManager.MIN_PLAYERS_COUNT && game.getCurrentPhase() == Phase.WAITING_FOR_PLAYERS) {
                    game.manualPhaseStep(Phase.PLAYERS_GATHERED)
                }
            }
        }
    }

    private suspend fun addPlayer(player: Player, psw: String?): Boolean {
        if (roomOwner == null)
            player.isRoomOwner = true
        if (!containsClient(player.clientId)) {
            if (psw != password) {
                player.socket.sendError("Incorrect password")
                return false
            }
            players = players.plus(player)
            val announcement = PlayerJoinedRoom(player.mapToPlayerState())
            broadcast(Gson().toJson(announcement), listOf(player))
            val playerStates = players.map {
                it.mapToPlayerState()
            }
            val roomState = RoomState(playerStates)
            player.socket.send(
                roomState.asJson()
            )
            return true
        } else {
            return if (disconnectedClients.contains(player.clientId)) {
                val playerToReconnect = players.first { it.clientId == player.clientId }
                playerToReconnect.socket = player.socket
                playerToReconnect.isConnected = true
                true
            } else {
                player.socket.sendError("Couldn't join you into the room either because their is already a player with this clientID or the password was incorrect")
                false
            }
        }
    }
    fun removePlayerFromGame(clientID: String) {
        launch {
            removePlayer(clientId = clientID)
            if (playerCount < GameManager.MIN_PLAYERS_COUNT && game.getCurrentPhase() == Phase.PLAYERS_GATHERED) {
                game.manualPhaseStep(Phase.WAITING_FOR_PLAYERS)
            }
            if(playerCount == 0){
                println("Should be deleted set to true becouse room is emopty")
                shouldBeDeleted = true
            }
        }
    }
    private suspend fun removePlayer(clientId: String) {
        val playerToRemove = players.find { it.clientId == clientId }
            ?: throw Exception("no player exists with this clientId to remove.")
        players = players.minus(playerToRemove)

        if (playerToRemove.isRoomOwner && players.isNotEmpty()) {
            players[0].isRoomOwner = true
        }
        val announcement = PlayerLeftRoom(playerToRemove.username, roomOwner?.username ?: "Unknown")
        broadcast(Gson().toJson(announcement))
    }

    fun findPlayer(username: String?): Player {
        return players.findLast { it.username == username } ?: throw PlayerNotFound()
    }

    fun findPlayerByPredicate(predicate: (Player) -> Boolean): Player {
        return players.findLast { predicate(it) } ?: throw PlayerNotFound()
    }

    fun mapPlayersListToPlayerState(): List<PlayerState> {
        return players.map {
            it.mapToPlayerState()
        }
    }

    fun startGame() {
        game.startGame()
    }
}


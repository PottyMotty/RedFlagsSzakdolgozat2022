package spoti.repo

import kotlinx.coroutines.*
import spoti.game.Room
import spoti.data.model.CreateRoomRequest
import spoti.game.GameManager
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

interface IRoomRepository {
    val rooms: ConcurrentHashMap<String, Room>
    fun createRoom(roomRequest: CreateRoomRequest): String

    fun handleRoomDeletion()
    fun getRandomRoomCode(): String
}

class RoomRepository(
    override val coroutineContext: CoroutineContext = Dispatchers.IO
) : CoroutineScope, IRoomRepository {
    companion object {
        private const val PATIENCE_TIME = 100000L
        private const val SCAN_INTERVAL = 30000L
    }

    override val rooms = ConcurrentHashMap<String, Room>()
    private val deleteJobs = ConcurrentHashMap<String, Job>()

    init {
        handleRoomDeletion()
    }

    override fun createRoom(roomRequest: CreateRoomRequest): String {
        val roomCode = getRandomRoomCode()
        val room = Room(password = roomRequest.password)
        room.createGame(packs = roomRequest.packs, numRounds = roomRequest.numRounds)
        rooms[roomCode] = room
        return roomCode
    }

    override fun handleRoomDeletion() {
        launch {
            while (true) {
                delay(SCAN_INTERVAL)
                rooms.forEach { (roomCode, room) ->
                    if (room.shouldBeDeleted) {
                        if(!deleteJobs.containsKey(roomCode)) {
                            deleteJobs[roomCode] = launch {
                                delay(PATIENCE_TIME)
                                if(room.shouldBeDeleted) {
                                    rooms.remove(roomCode)
                                    println("game with code $roomCode deleted")
                                }
                            }
                        }
                    } else {
                        if (deleteJobs.containsKey(roomCode)) {
                            deleteJobs[roomCode]?.cancel()
                            deleteJobs.remove(roomCode)
                        }
                    }
                }
            }
        }
    }

    private val charPool = ('A'..'Z') + ('0'..'9')
    private fun randomText(length: Int): String {
        return (1..length)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    override fun getRandomRoomCode(): String {
        var roomCodeCandidate = randomText(6)
        while (rooms.containsKey(roomCodeCandidate)) {
            roomCodeCandidate = randomText(6)
        }
        return roomCodeCandidate
    }
}
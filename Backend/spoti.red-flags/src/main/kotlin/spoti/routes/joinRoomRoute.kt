package spoti.routes


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.koin.ktor.ext.inject
import spoti.data.model.BasicResponse
import spoti.other.Constants
import spoti.other.Phase
import spoti.repo.RoomRepository


fun Route.joinRoomRoute(){
    val roomRepo: RoomRepository by inject()
    route("/api/checkRoom/{roomCode}"){
        get {
            val roomCode = call.parameters["roomCode"]
            val username = call.request.queryParameters["username"] ?: ""
            val clientID = call.request.queryParameters["client_id"]
            if(roomCode == null){
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val room = roomRepo.rooms[roomCode]
            val playerWithThisClientID = room?.players?.find { it.clientId == clientID }
            val playerWithThisName = room?.players?.find { it.username == username }
            val nameTaken = playerWithThisName!= null && playerWithThisName.clientId != clientID
            val playerRejoining = playerWithThisClientID != null

            when{
                room ==null -> {
                    call.respond(HttpStatusCode.NotFound, BasicResponse(false,"Room with this code not found"))
                }
                nameTaken ->{
                    call.respond(HttpStatusCode.Conflict, BasicResponse(false,"Room already has a user with this name"))
                }
                room.players.count()>=Constants.MAX_ROOM_SIZE ->{
                    call.respond(HttpStatusCode.Conflict, BasicResponse(false,"Room is already at full capacity"))
                }
                !playerRejoining && !(room.game.getCurrentPhase() == Phase.WAITING_FOR_PLAYERS || room.game.getCurrentPhase() == Phase.PLAYERS_GATHERED) ->{
                    call.respond(HttpStatusCode.MethodNotAllowed, BasicResponse(false,"Game is already in play"))
                }
                else ->
                    call.respond(HttpStatusCode.OK, BasicResponse(true, "Good to go"))
            }
        }
    }
    route("/api/isPasswordProtected/{roomCode}"){
        get{
            val roomCode = call.parameters["roomCode"]
            val room = roomRepo.rooms[roomCode]
            if(room != null) {
                call.respond(HttpStatusCode.OK, room.isPasswordProtected)
            }
        }
    }
}
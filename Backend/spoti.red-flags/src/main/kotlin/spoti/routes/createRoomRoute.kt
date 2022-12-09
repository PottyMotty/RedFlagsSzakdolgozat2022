package spoti.routes


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.koin.ktor.ext.inject

import spoti.data.model.CreateRoomRequest
import spoti.data.model.RoomCode
import spoti.repo.RoomRepository

fun Route.createRoomRoute(){
    val roomRepo: RoomRepository by inject()
    route("/api/createRoom"){
        post{
            val roomRequest =call.receiveOrNull<CreateRoomRequest>()
            if(roomRequest==null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val code=roomRepo.createRoom(roomRequest)
            call.respond(RoomCode(code))
        }
    }
}



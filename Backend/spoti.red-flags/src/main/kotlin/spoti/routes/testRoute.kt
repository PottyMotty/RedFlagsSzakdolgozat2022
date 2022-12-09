package spoti.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import spoti.data.model.websocket.`in`.JoinRoomHandshake
import kotlin.text.get

fun Route.test()
{
    route("/api/test"){
        get{
            call.respond(HttpStatusCode.OK, "Server up and running devMode: ${call.application.environment.developmentMode}")
        }
    }
}
package spoti.plugins


import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import spoti.session.PlayerSession

fun Application.configureSessions(){
    install(Sessions){
        cookie<PlayerSession>("SESSION")
    }
    intercept(ApplicationCallPipeline.Features){
        if (call.sessions.get<PlayerSession>()==null){
            val clientID = call.parameters["client_id"]
            if(clientID!=null)
                call.sessions.set(PlayerSession(clientID, generateNonce()))
        }
    }
}
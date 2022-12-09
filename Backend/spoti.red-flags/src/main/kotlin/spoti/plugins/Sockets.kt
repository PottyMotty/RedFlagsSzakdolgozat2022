package spoti.plugins

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*


fun Application.configureSockets() {
    install(WebSockets){
        pingPeriodMillis=1000
    }
}

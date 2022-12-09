package spoti


import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.ktorm.entity.forEach
import spoti.di.gameModule

import spoti.plugins.*
import spoti.repo.CardRepository
import spoti.routes.*
import spoti.service.CardServiceImpl
import java.time.Duration

fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain.main(args)
@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(WebSockets){
        pingPeriodMillis = 3000L
        timeoutMillis = 3000L
    }
    install(Routing){
        createRoomRoute()
        joinRoomRoute()
        webSocketRoute()
        test()
        imageRoute()
        cardRoute()
    }
    install(Koin){
        modules(gameModule)
    }
    configureSerialization()
}

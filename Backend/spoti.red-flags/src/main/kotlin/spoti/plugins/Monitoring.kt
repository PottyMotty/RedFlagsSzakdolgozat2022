package spoti.plugins

import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*

import org.slf4j.event.*


fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

}

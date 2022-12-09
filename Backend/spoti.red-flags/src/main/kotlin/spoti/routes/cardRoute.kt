package spoti.routes

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import spoti.repo.CardRepository
import spoti.service.CardServiceImpl
import kotlin.text.get

fun Route.cardRoute(){
    val cardService: CardServiceImpl by inject()
    route("/api/packs"){
        get {
            val packs=cardService.GetPacks()
            call.respond(HttpStatusCode.OK,packs)
        }
    }
}
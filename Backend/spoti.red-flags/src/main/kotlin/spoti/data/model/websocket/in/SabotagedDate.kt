package spoti.data.model.websocket.`in`

import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.update
import spoti.game.Room
import spoti.data.model.websocket.IIncomingPackage
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType
import java.util.concurrent.atomic.AtomicInteger

data class SabotagedDate(var negative: String) : WebsocketRequestModel(MessageType.DATE_SABOTAGE), IIncomingPackage {
    override fun handlePackage(socket: DefaultWebSocketServerSession, clientId: String, room: Room) {
        room.game.let { game ->
            val sender = room.findPlayerByPredicate { it.clientId == clientId }
            println("Sender: ${sender.username}")
            val sabotagedPerson = game.sabotagePairing[sender.username]
            println("SabotagedPerson: ${sabotagedPerson}")
            println("pairings: ${game.sabotagePairing}")
            val sabotagedDate = game.dateOptions.findLast { it.createdBy == sabotagedPerson }
            println("SabotagedDAte: ${sabotagedDate?.positiveAttributes}")
            sabotagedDate?.apply {
                negativeAttribute = negative
                sabotagedBy = sender.username
            }
            game.doneWorking.update { AtomicInteger(it.incrementAndGet()) }
        }
    }
}
package spoti.data.model.websocket.`in`

import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.update
import spoti.game.Room
import spoti.data.model.websocket.DateInfo
import spoti.data.model.websocket.IIncomingPackage
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType
import java.util.concurrent.atomic.AtomicInteger

data class CreatedDate(val positiveAttributes: List<String>) : WebsocketRequestModel(MessageType.CREATED_DATE), IIncomingPackage {
    override fun handlePackage(socket: DefaultWebSocketServerSession, clientId: String, room: Room) {
        room.game.let { game ->
            val sender = room.findPlayerByPredicate { it.clientId == clientId }
            val previousDate = game.dateOptions.findLast { date -> date.createdBy == sender.username }
            val dateToAdd = DateInfo(
                positiveAttributes = positiveAttributes,
                negativeAttribute = null,
                createdBy = sender.username,
                sabotagedBy = null
            )
            if (previousDate != null) {
                game.dateOptions -= previousDate
            }
            game.dateOptions += dateToAdd
            game.doneWorking.update { AtomicInteger(it.incrementAndGet()) }
        }
    }
}
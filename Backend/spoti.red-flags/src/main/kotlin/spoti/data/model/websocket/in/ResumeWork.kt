package spoti.data.model.websocket.`in`

import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.update
import spoti.game.Room
import spoti.data.model.websocket.IIncomingPackage
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType
import java.util.concurrent.atomic.AtomicInteger

class ResumeWork(): WebsocketRequestModel(MessageType.RESUME_WORK),IIncomingPackage {
    override fun handlePackage(socket: DefaultWebSocketServerSession, clientId: String, room: Room) {
        room.game.doneWorking.update { AtomicInteger(it.decrementAndGet()) }
    }
}
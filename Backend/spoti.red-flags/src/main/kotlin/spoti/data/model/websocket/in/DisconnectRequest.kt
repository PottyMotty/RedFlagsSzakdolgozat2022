package spoti.data.model.websocket.`in`

import io.ktor.server.websocket.*
import spoti.game.Room
import spoti.data.model.websocket.IIncomingPackage
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

class DisconnectRequest() : WebsocketRequestModel(MessageType.DISCONNECT_REQUEST), IIncomingPackage{
    override fun handlePackage(socket: DefaultWebSocketServerSession, clientId: String, room: Room) {
        room.removePlayerFromGame(clientId)
    }
}
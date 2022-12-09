package spoti.data.model.websocket.`in`

import io.ktor.server.websocket.*
import spoti.data.Player
import spoti.game.Room
import spoti.data.model.websocket.IIncomingPackage
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

class ReconnectRequest() : WebsocketRequestModel(MessageType.RECONNECT_REQUEST), IIncomingPackage {
    override fun handlePackage(socket: DefaultWebSocketServerSession, clientId: String, room: Room) {
        val canBeReconnected = room.isClientDisconnected(clientId)
        if(!canBeReconnected)
            throw Exception("Can't reconnect because this client is not disconnected")
        val oldPlayer = room.findPlayerByPredicate { it.clientId == clientId }
        val player = Player(
            username = oldPlayer.username,
            imageURL = oldPlayer.imageURL,
            socket = socket,
            clientId = clientId,
            isConnected = true,
            points = 0
        )
        room.addPlayerToGame(player, null)
    }

}
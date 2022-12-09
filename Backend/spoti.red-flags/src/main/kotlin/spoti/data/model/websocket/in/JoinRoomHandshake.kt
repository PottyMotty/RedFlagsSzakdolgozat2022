package spoti.data.model.websocket.`in`

import io.ktor.server.websocket.*
import spoti.data.Player
import spoti.game.Room
import spoti.data.model.websocket.IIncomingPackage
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class JoinRoomHandshake(val username: String,
                             val imgURL: String,
                             val clientID: String,
                             val password: String)
    :WebsocketRequestModel(MessageType.JOIN_ROOM_HANDSHAKE), IIncomingPackage {
    override fun handlePackage(socket: DefaultWebSocketServerSession, clientId: String, room: Room) {
        val player = Player(
            username = username,
            imageURL = imgURL,
            socket = socket,
            clientId = clientID,
            isConnected = true,
            points = 0
        )
        room.addPlayerToGame(player, password)
    }
}
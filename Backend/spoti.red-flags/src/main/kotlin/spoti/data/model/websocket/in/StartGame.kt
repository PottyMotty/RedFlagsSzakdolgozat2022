package spoti.data.model.websocket.`in`

import io.ktor.server.websocket.*
import spoti.game.Room
import spoti.data.model.websocket.IIncomingPackage
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

class StartGame : WebsocketRequestModel(MessageType.START_GAME), IIncomingPackage {
    override fun handlePackage(socket: DefaultWebSocketServerSession, clientId: String, room: Room) {
        if(room.roomOwner?.clientId==clientId)
            room.startGame()
    }
}
package spoti.data.model.websocket.`in`

import io.ktor.server.websocket.*
import spoti.game.Room
import spoti.data.model.websocket.DateInfo
import spoti.data.model.websocket.IIncomingPackage
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class WinnerChoosen(val winnerDateOption: DateInfo) : WebsocketRequestModel(MessageType.WINNER_CHOOSEN),IIncomingPackage {
    override fun handlePackage(socket: DefaultWebSocketServerSession, clientId: String, room: Room) {
        if(clientId==room.game.single?.clientId)
            room.game.winnerSelected(winnerDateOption)
    }
}
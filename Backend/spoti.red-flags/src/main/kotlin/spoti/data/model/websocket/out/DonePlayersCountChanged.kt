package spoti.data.model.websocket.out

import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class DonePlayersCountChanged(val count: Int) : WebsocketRequestModel(MessageType.DONE_COUNT_CHANGED){
}
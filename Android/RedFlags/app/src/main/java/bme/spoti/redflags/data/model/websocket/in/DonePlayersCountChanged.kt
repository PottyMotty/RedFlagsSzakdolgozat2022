package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType

data class DonePlayersCountChanged(val count: Int) : WebsocketRequestModel(MessageType.DONE_COUNT_CHANGED){
}
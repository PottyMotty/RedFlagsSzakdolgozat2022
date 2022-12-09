package spoti.data.model.websocket.out

import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class ErrorAnnouncement(val message: String) : WebsocketRequestModel(MessageType.ERROR)
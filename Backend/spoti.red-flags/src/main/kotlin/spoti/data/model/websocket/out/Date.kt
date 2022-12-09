package spoti.data.model.websocket.out

import spoti.data.model.websocket.DateInfo
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class Date(val date : DateInfo) : WebsocketRequestModel(MessageType.ONE_DATE)
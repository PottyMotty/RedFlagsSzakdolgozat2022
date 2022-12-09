package spoti.data.model.websocket.out

import spoti.data.model.websocket.DateInfo
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class AllDates(var dates: List<DateInfo>) : WebsocketRequestModel(MessageType.ALL_DATES){
}
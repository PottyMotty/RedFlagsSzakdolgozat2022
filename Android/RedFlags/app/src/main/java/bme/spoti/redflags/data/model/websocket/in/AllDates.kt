package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType


data class AllDates(var dates: List<DateInfo>) : WebsocketRequestModel(MessageType.ALL_DATES){
}
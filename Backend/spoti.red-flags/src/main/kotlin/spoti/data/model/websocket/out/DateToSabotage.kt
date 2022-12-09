package spoti.data.model.websocket.out

import spoti.data.model.websocket.DateInfo
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class DateToSabotage(val dateInfoToSabotage: DateInfo): WebsocketRequestModel(MessageType.SABOTAGE_MATERIAL) {
}
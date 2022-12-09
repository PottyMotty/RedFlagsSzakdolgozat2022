package bme.spoti.redflags.data.model.websocket.out

import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType

data class SabotagedDate(val negative: String) : WebsocketRequestModel(MessageType.DATE_SABOTAGE)
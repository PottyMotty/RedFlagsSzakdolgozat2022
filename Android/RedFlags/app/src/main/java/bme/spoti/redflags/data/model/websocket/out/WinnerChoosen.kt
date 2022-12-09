package bme.spoti.redflags.data.model.websocket.out

import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType

data class WinnerChoosen(val winnerDateOption: DateInfo) : WebsocketRequestModel(MessageType.WINNER_CHOOSEN)
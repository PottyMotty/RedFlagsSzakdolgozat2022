package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType


data class WinnerAnnouncement(val winnerDateOption: DateInfo): WebsocketRequestModel(MessageType.WINNER_ANNOUNCMENT)
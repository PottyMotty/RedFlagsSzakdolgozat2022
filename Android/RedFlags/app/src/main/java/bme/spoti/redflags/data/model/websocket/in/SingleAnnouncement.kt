package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType


class SingleAnnouncement(val singlePlayer: PlayerState) : WebsocketRequestModel(MessageType.SINGLE_ANNOUNCMENT){
}
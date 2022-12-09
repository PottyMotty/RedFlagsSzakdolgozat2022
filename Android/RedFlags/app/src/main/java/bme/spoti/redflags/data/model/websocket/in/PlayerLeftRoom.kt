package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType


data class PlayerLeftRoom(val leftUsername: String, val newRoomOwner : String): WebsocketRequestModel(
    MessageType.PLAYER_LEFT)

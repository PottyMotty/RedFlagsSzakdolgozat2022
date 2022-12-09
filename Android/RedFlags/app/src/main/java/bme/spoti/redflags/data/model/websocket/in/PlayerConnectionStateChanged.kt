package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType

data class PlayerConnectionStateChanged(
    val players: List<PlayerState>
): WebsocketRequestModel(MessageType.CONNECTION_STATE_CHANGED)
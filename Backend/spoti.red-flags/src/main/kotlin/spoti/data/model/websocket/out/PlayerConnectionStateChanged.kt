package spoti.data.model.websocket.out

import spoti.data.model.websocket.PlayerState
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class PlayerConnectionStateChanged(
    val players: List<PlayerState>
): WebsocketRequestModel(MessageType.CONNECTION_STATE_CHANGED)
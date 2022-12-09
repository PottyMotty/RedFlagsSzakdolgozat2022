package spoti.data.model.websocket.out

import spoti.data.model.websocket.PlayerState
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class RoomState(val players: List<PlayerState>): WebsocketRequestModel(MessageType.ROOM_STATE)
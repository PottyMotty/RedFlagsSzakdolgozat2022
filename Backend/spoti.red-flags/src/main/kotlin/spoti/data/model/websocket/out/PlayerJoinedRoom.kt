package spoti.data.model.websocket.out

import spoti.data.model.websocket.PlayerState
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class PlayerJoinedRoom(val joinedPlayer: PlayerState): WebsocketRequestModel(MessageType.PLAYER_JOINED) {}
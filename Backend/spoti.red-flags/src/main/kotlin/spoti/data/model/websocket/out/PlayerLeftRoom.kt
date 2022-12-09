package spoti.data.model.websocket.out

import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class PlayerLeftRoom(val leftUsername: String, val newRoomOwner : String): WebsocketRequestModel(MessageType.PLAYER_LEFT)

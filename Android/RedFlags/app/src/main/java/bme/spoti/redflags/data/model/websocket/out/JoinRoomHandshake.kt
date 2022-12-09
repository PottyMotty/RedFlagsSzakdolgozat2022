package bme.spoti.redflags.data.model.websocket.out


import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType

data class JoinRoomHandshake(val username: String,
                             val imgURL: String,
                             val clientID: String,
                             val password: String)
    : WebsocketRequestModel(MessageType.JOIN_ROOM_HANDSHAKE)
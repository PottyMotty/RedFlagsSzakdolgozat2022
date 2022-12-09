package bme.spoti.redflags.data.model.websocket.out

import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType

class ReconnectRequest : WebsocketRequestModel(MessageType.RECONNECT_REQUEST) {
}
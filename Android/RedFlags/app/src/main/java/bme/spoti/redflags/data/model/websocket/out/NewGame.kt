package bme.spoti.redflags.data.model.websocket.out


import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType

class NewGame : WebsocketRequestModel(MessageType.NEW_GAME)
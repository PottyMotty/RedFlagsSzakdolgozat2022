package spoti.data.model.websocket.out

import spoti.data.model.websocket.PlayerState
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

class SingleAnnouncement(val singlePlayer: PlayerState) : WebsocketRequestModel(MessageType.SINGLE_ANNOUNCMENT){
}
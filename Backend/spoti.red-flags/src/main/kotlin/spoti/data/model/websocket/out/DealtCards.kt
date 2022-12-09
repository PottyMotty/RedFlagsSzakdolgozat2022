package spoti.data.model.websocket.out

import spoti.data.model.CardData
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class DealtCards(var cards: List<CardData>) : WebsocketRequestModel(MessageType.DEALT_CARDS){
}
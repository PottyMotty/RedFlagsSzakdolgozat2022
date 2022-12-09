package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.model.CardData
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType


data class DealtCards(var cards: List<CardData>) : WebsocketRequestModel(MessageType.DEALT_CARDS){
}
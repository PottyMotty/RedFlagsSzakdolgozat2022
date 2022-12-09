package spoti.data

import io.ktor.websocket.*
import spoti.data.model.CardData
import spoti.data.model.websocket.PlayerState


data class Player(
    val username: String,
    val imageURL: String,
    var socket: WebSocketSession,
    val clientId: String,
    var isRoomOwner: Boolean =false,
    var isConnected: Boolean=true,
    var points: Int=0,
    ){
    var pointsThisRound : Int=0
    var cardsInHand: List<CardData> = emptyList()

    fun reset(){
        points=0
        pointsThisRound=0
    }
}

fun Player.mapToPlayerState() : PlayerState{
    return PlayerState(
        username = this.username,
        imageURL = this.imageURL,
        roomOwner = this.isRoomOwner,
        isConnected = isConnected
    )
}


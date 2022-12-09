package spoti.data.model.websocket

import io.ktor.server.websocket.*
import spoti.game.Room

import spoti.other.MessageType

abstract class WebsocketRequestModel(val type: MessageType) {}
interface IIncomingPackage{
    fun handlePackage(
        socket: DefaultWebSocketServerSession,
        clientId: String,
        room: Room
    )
}
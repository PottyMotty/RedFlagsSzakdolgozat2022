package spoti.routes

import com.google.gson.Gson
import com.google.gson.JsonParser
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject
import spoti.data.model.websocket.`in`.*
import spoti.extensions.sendError
import spoti.other.MessageType
import spoti.repo.RoomRepository
import java.lang.Exception

fun Route.webSocketRoute(){
    val roomRepo : RoomRepository by inject()
    val gson: Gson by inject()
    route("/ws/game/{roomCode}"){
        webSocket {
            val roomCode = call.parameters["roomCode"]
            val clientID = call.request.queryParameters["client_id"]
            when{
                clientID==null -> {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY,"No clientID. :("))
                    return@webSocket
                }
                roomCode==null -> {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY,"No roomcode. :("))
                    return@webSocket
                }
            }
            val room= roomRepo.rooms[roomCode]
            if(room==null){
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY,"No room with this code. :("))
                return@webSocket
            }
            try {
                incoming.consumeEach { frame ->
                    if(frame is Frame.Text){
                        val msg = frame.readText()
                        val msgToJson = JsonParser.parseString(msg).asJsonObject
                        val typeObj = gson.fromJson(msgToJson.getAsJsonPrimitive("type"),MessageType::class.java)
                        val type = when(typeObj){
                            MessageType.JOIN_ROOM_HANDSHAKE -> JoinRoomHandshake::class.java
                            MessageType.DISCONNECT_REQUEST -> DisconnectRequest::class.java
                            MessageType.START_GAME -> StartGame::class.java
                            MessageType.RESUME_WORK -> ResumeWork::class.java
                            MessageType.CREATED_DATE -> CreatedDate::class.java
                            MessageType.DATE_SABOTAGE -> SabotagedDate::class.java
                            MessageType.WINNER_CHOOSEN -> WinnerChoosen::class.java
                            MessageType.RECONNECT_REQUEST -> ReconnectRequest::class.java
                            else -> throw Exception("Big oops")
                        }
                        val payload =gson.fromJson(msg,type)
                        payload.handlePackage(this,clientID!!,room)
                    }
                }
            }catch (e: Exception){
                this.sendError(e.message ?: "Unknown error")
                e.printStackTrace()
            } finally {
                clientID?.let{
                    room.handleConnectionLost(it)
                }
            }
        }
    }
}

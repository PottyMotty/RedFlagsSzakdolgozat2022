package bme.spoti.redflags.network.ws

import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.data.model.websocket.`in`.*
import bme.spoti.redflags.data.model.websocket.out.*
import bme.spoti.redflags.other.MessageType
import com.google.gson.*
import timber.log.Timber

import java.lang.reflect.Type


class CustomWebSocketRequestModelAdapter : JsonDeserializer<WebsocketRequestModel>, JsonSerializer<WebsocketRequestModel>{
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WebsocketRequestModel {
        val typeObj = context?.deserialize<MessageType>(
            json?.asJsonObject?.getAsJsonPrimitive("type"),
            MessageType::class.java
        )
        val type = when (typeObj) {
            MessageType.PLAYER_JOINED -> PlayerJoinedRoom::class.java
            MessageType.PLAYER_LEFT -> PlayerLeftRoom::class.java
            MessageType.ROOM_STATE -> RoomState::class.java
            MessageType.SINGLE_ANNOUNCMENT -> SingleAnnouncement::class.java
            MessageType.DONE_COUNT_CHANGED -> DonePlayersCountChanged::class.java
            MessageType.WINNER_ANNOUNCMENT -> WinnerAnnouncement::class.java
            MessageType.DEALT_CARDS -> DealtCards::class.java
            MessageType.ALL_DATES -> AllDates::class.java
            MessageType.SABOTAGE_MATERIAL -> DateToSabotage::class.java
            MessageType.LEADERBOARD -> LeaderBoardData::class.java
            MessageType.PHASE_CHANGE -> PhaseChange::class.java
            MessageType.ERROR -> ErrorAnnouncement::class.java
            MessageType.CONNECTION_STATE_CHANGED -> PlayerConnectionStateChanged::class.java
            MessageType.TIME_LEFT -> TimeLeftOfPhase::class.java
            MessageType.GAME_STATE_REMINDER -> GameStateReminder::class.java
            else -> throw Exception("Unknown message type ${typeObj?.name}")
        }
        return context.deserialize(json, type)
    }

    override fun serialize(
        src: WebsocketRequestModel?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        var outgoingRequestModel = src
        outgoingRequestModel =when(src?.type){
            MessageType.JOIN_ROOM_HANDSHAKE -> outgoingRequestModel as JoinRoomHandshake
            MessageType.RECONNECT_REQUEST -> outgoingRequestModel as ReconnectRequest
            MessageType.DISCONNECT_REQUEST ->  outgoingRequestModel as DisconnectRequest
            MessageType.START_GAME ->  outgoingRequestModel as StartGame
            MessageType.CREATED_DATE ->  outgoingRequestModel as CreatedDate
            MessageType.DATE_SABOTAGE -> outgoingRequestModel as SabotagedDate
            MessageType.RESUME_WORK ->  outgoingRequestModel as ResumeWork
            MessageType.WINNER_CHOOSEN ->  outgoingRequestModel as WinnerChoosen
            MessageType.NEW_GAME ->  outgoingRequestModel as NewGame
            else-> throw Exception("Unknown type outgoing")
        }
        return context?.serialize(outgoingRequestModel) ?: throw Exception("Parse error")
    }

}


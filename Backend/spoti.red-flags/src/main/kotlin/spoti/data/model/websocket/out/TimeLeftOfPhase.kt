package spoti.data.model.websocket.out

import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType
import spoti.other.Phase

data class TimeLeftOfPhase(
    val timeLeft: Long,
    val phase: Phase
) : WebsocketRequestModel(MessageType.TIME_LEFT)
package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.Game
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType


data class TimeLeftOfPhase(
    val timeLeft: Long,
    val phase: Game.Phase
) : WebsocketRequestModel(MessageType.TIME_LEFT)
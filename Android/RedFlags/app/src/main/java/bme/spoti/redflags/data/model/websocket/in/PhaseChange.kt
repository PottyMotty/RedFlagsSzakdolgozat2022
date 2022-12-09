package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.Game
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType


data class PhaseChange(var phase: Game.Phase, val phaseDuration : Long?) : WebsocketRequestModel(MessageType.PHASE_CHANGE)
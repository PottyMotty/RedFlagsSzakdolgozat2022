package spoti.data.model.websocket.out

import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType
import spoti.other.Phase

data class PhaseChange(var phase: Phase, val phaseDuration : Long? = null) : WebsocketRequestModel(MessageType.PHASE_CHANGE)
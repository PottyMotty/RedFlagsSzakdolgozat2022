package bme.spoti.redflags.data.model.websocket

import kotlinx.serialization.Serializable

@Serializable
data class LeaderBoardEntry(val username: String, val points: Int, val pointsThisRound: Int)

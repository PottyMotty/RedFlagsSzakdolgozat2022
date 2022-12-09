package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.model.websocket.LeaderBoardEntry
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType


data class LeaderBoardData(val leaderBoard: List<LeaderBoardEntry>): WebsocketRequestModel(
    MessageType.LEADERBOARD)

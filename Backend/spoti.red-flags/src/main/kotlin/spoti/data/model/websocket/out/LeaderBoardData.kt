package spoti.data.model.websocket.out

import spoti.data.model.websocket.LeaderBoardEntry
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType

data class LeaderBoardData(val leaderBoard: List<LeaderBoardEntry>): WebsocketRequestModel(MessageType.LEADERBOARD)

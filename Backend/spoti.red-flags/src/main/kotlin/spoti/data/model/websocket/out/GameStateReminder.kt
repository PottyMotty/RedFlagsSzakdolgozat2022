package spoti.data.model.websocket.out

import spoti.data.model.CardData
import spoti.data.model.websocket.DateInfo
import spoti.data.model.websocket.LeaderBoardEntry
import spoti.data.model.websocket.PlayerState
import spoti.data.model.websocket.WebsocketRequestModel
import spoti.other.MessageType
import spoti.other.Phase

data class GameStateReminder(
    val players : List<PlayerState>,
    val phase: Phase,
    val personalData : PlayerState,
    val singlePlayer : PlayerState?,
    val remainingTime: Long?,
    val cardsInHand: List<CardData>,
    val lastSubmittedDate: DateInfo?,
    val lastSabotagedDate: DateInfo?,
    val allDates : List<DateInfo>,
    val winnerDate : DateInfo?,
    val dateToSabotage: DateInfo?,
    val leaderBoardStanding: List<LeaderBoardEntry>
) : WebsocketRequestModel(MessageType.GAME_STATE_REMINDER)
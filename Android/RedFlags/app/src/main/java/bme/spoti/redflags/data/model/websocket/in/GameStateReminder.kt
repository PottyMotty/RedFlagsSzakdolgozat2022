package bme.spoti.redflags.data.model.websocket.`in`

import bme.spoti.redflags.data.Game
import bme.spoti.redflags.data.model.CardData
import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.data.model.websocket.LeaderBoardEntry
import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.MessageType

data class GameStateReminder(
    val players : List<PlayerState>,
    val phase: Game.Phase,
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
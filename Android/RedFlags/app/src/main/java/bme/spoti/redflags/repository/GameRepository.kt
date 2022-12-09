package bme.spoti.redflags.repository

import bme.spoti.redflags.data.Game
import bme.spoti.redflags.data.model.CardData
import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.data.model.websocket.LeaderBoardEntry
import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

interface GameRepository {
    val roomCode : Flow<String?>
    val personalInfo : MutableStateFlow<PlayerState?>
    val playersInRoom : MutableStateFlow<List<PlayerState>>
    val singlePlayer : MutableStateFlow<PlayerState?>
    val cardsInHand : MutableStateFlow<List<CardData>>
    val doneCount : MutableStateFlow<Int>
    val phaseDuration : MutableStateFlow<Long?>
    val phaseRemainingTime : MutableStateFlow<Long?>
    val phase : MutableStateFlow<Game.Phase>
    val allDates : MutableStateFlow<List<DateInfo>>
    val dateToSabotage : MutableStateFlow<DateInfo?>
    val errorFlow : MutableSharedFlow<String>
    val leaderBoardStandings : MutableStateFlow<List<LeaderBoardEntry>>
    val winnerDate : MutableStateFlow<DateInfo?>
    val lastSubmittedDate : MutableStateFlow<DateInfo?>
    val lastSabotagedDate : MutableStateFlow<DateInfo?>

    val calculatedPhase : Flow<Game.Phase>

    fun resetForNewRound()
    suspend fun clearAll(includingRoomCode: Boolean)
}
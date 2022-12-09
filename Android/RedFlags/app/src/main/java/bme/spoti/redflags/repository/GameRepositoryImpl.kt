package bme.spoti.redflags.repository

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import bme.spoti.redflags.data.Game
import bme.spoti.redflags.data.model.CardData
import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.data.model.websocket.LeaderBoardEntry
import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.network.ws.RedFlagsSocketServiceImpl
import bme.spoti.redflags.network.ws.SocketState
import bme.spoti.redflags.utils.deleteRoom
import bme.spoti.redflags.utils.roomIdFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

import kotlin.coroutines.CoroutineContext

class GameRepositoryImpl(
	private val redFlagsSocketService: RedFlagsSocketService,
	private val preferencesDataStore: DataStore<Preferences>,
) : GameRepository {




	override val roomCode: Flow<String?> = preferencesDataStore.roomIdFlow()
	override val personalInfo: MutableStateFlow<PlayerState?> = MutableStateFlow(null)
	override val playersInRoom: MutableStateFlow<List<PlayerState>> = MutableStateFlow(emptyList())
	override val singlePlayer: MutableStateFlow<PlayerState?> = MutableStateFlow(null)
	override val cardsInHand: MutableStateFlow<List<CardData>> = MutableStateFlow(emptyList())
	override val doneCount: MutableStateFlow<Int> = MutableStateFlow(0)
	override val phaseDuration: MutableStateFlow<Long?> = MutableStateFlow(null)
	override val phaseRemainingTime: MutableStateFlow<Long?> = MutableStateFlow(null)
	override val phase: MutableStateFlow<Game.Phase> =
		MutableStateFlow(Game.Phase.WAITING_FOR_PLAYERS)
	override val allDates: MutableStateFlow<List<DateInfo>> = MutableStateFlow(emptyList())
	override val dateToSabotage: MutableStateFlow<DateInfo?> = MutableStateFlow(null)
	override val errorFlow: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1)
	override val leaderBoardStandings: MutableStateFlow<List<LeaderBoardEntry>> =
		MutableStateFlow(emptyList())
	override val winnerDate: MutableStateFlow<DateInfo?> = MutableStateFlow(null)
	override val lastSubmittedDate: MutableStateFlow<DateInfo?> = MutableStateFlow(null)
	override val lastSabotagedDate: MutableStateFlow<DateInfo?> = MutableStateFlow(null)
	override val calculatedPhase: Flow<Game.Phase> = combine(
		phase,
		playersInRoom,
		redFlagsSocketService.socketStateFlow
	) { phase, players, socketState ->
		if (socketState != SocketState.Connected) {
			Game.Phase.RECONNECT
		} else if (players.any { !it.isConnected }) {
			Game.Phase.WAITING_FOR_RECONNECT
		} else {
			phase
		}
	}.distinctUntilChanged()

	override fun resetForNewRound() {
		singlePlayer.update { null }
		cardsInHand.update { emptyList() }
		allDates.update { emptyList() }
		dateToSabotage.update { null }
		winnerDate.update { null }
		lastSubmittedDate.update { null }
		lastSabotagedDate.update { null }
	}

	override suspend fun clearAll(includingRoomCode: Boolean) {
		if(includingRoomCode) {
			preferencesDataStore.deleteRoom()
		}
		personalInfo.update { null }
		playersInRoom.update { emptyList() }
		singlePlayer.update { null }
		cardsInHand.update { emptyList() }
		doneCount.update { 0 }
		phaseDuration.update { null }
		phaseRemainingTime.update { null }
		phase.update { Game.Phase.WAITING_FOR_PLAYERS }
		allDates.update { emptyList() }
		dateToSabotage.update { null }
		leaderBoardStandings.update { emptyList() }
		winnerDate.update { null }
		lastSubmittedDate.update { null }
		lastSabotagedDate.update { null }
	}

}

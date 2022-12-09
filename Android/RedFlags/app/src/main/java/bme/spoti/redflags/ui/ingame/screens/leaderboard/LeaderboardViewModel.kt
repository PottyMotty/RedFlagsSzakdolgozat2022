package bme.spoti.redflags.ui.ingame.screens.leaderboard

import androidx.lifecycle.ViewModel
import bme.spoti.redflags.data.model.websocket.LeaderBoardEntry
import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.repository.GameRepository
import kotlinx.coroutines.flow.combine

class LeaderboardViewModel(
	private val gameRepository: GameRepository
) : ViewModel() {
	val leaderboardData = combine(
		gameRepository.leaderBoardStandings,
		gameRepository.playersInRoom
	){standing, players ->
		standing.map { entry->
			PlayerDataWithScores(
				player = players.first{it.username == entry.username},
				leaderBoardEntry = entry
			)
		}
	}

	fun resetForNewRound(){
		gameRepository.resetForNewRound()
	}
}

data class PlayerDataWithScores(
	val player : PlayerState,
	val leaderBoardEntry: LeaderBoardEntry
)
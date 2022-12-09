package bme.spoti.redflags.ui.ingame.screens.end

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.repository.GameRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EndViewModel(
	private val gameRepository: GameRepository
) : ViewModel() {
	val winner = gameRepository.leaderBoardStandings.map { it.first() }

	suspend fun cleanUp() {
		gameRepository.clearAll(true)
	}
}
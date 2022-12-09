package bme.spoti.redflags.ui.ingame.screens.winner_showcase

import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.ui.base.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

class WinnerAnnouncementViewModel(
	private val gameRepository: GameRepository
) : BaseViewModel(){
	val winnerPlayer = combine(gameRepository.winnerDate.filterNotNull(), gameRepository.playersInRoom){winner, players->
		players.find { it.username == winner.createdBy }
	}

}
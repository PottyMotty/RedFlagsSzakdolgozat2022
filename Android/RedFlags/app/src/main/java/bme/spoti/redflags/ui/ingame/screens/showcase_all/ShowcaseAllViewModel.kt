package bme.spoti.redflags.ui.ingame.screens.showcase_all

import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.data.model.websocket.out.WinnerChoosen
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.ui.base.BaseViewModel
import bme.spoti.redflags.utils.asJson
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ShowcaseAllViewModel(
	private val gameRepository: GameRepository,
	private val redFlagsSocketService: RedFlagsSocketService
) : BaseViewModel() {


	val iAmSingle = combine(
		gameRepository.singlePlayer.filterNotNull(),
		gameRepository.personalInfo.filterNotNull()
	) { single, personalInfo ->
		single.username == personalInfo.username
	}

	val allDates = gameRepository.allDates
	val players = gameRepository.playersInRoom

	fun sendMessage(winnerChoosen: WinnerChoosen) {
		viewModelScope.launch {
			redFlagsSocketService.sendMessage(winnerChoosen)
		}
	}
}
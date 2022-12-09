package bme.spoti.redflags.ui.ingame.screens.showcase_one

import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.data.Game
import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.data.model.websocket.out.WinnerChoosen
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.ui.base.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.ceil

class ShowcaseOneViewModel(
	private val gameRepository: GameRepository,
	private val redFlagsSocketService: RedFlagsSocketService
) : BaseViewModel() {
	private val allDatesSizeFlow = gameRepository.allDates.map { it.size }.distinctUntilChanged()

	val dates = gameRepository.allDates.filter { it.isNotEmpty() }.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(), emptyList()
	)
	private val duration = gameRepository.phaseDuration.filterNotNull().shareIn(
		viewModelScope,
		SharingStarted.WhileSubscribed()
	)

	private val remaining = gameRepository.phaseRemainingTime.filterNotNull()
		.shareIn(viewModelScope, SharingStarted.WhileSubscribed())

	val showcaseState = gameRepository.phase.map {
		when (it) {
			Game.Phase.SHOWCASE_ONE_BY_ONE -> ShowcaseState.SHOWCASE_ONE_BY_ONE
			Game.Phase.SHOWCASE_ALL -> ShowcaseState.SHOWCASE_ALL
			else -> null
		}
	}.asLiveData()

	val iAmSingle = combine(
		gameRepository.singlePlayer.filterNotNull(),
		gameRepository.personalInfo.filterNotNull()
	) { single, personalInfo ->
		single.username == personalInfo.username
	}

	val dateToShowcase = combine(
		dates.filter { it.isNotEmpty() },
		remaining,
		duration,
	) { allDates, remainingTime, phaseDuration ->
		val timeForOne = phaseDuration.toInt() / (allDates.size)
		var index =
			allDates.size - ceil(remainingTime.toDouble() / timeForOne.toDouble()).toInt()
		if (index >= allDates.size) {
			index = allDates.size - 1
		}
		Timber.d("TIME: ${remainingTime.toDouble()} / ${timeForOne.toDouble()}")
		index
	}.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

	val creator =
		combine(dateToShowcase, gameRepository.playersInRoom, dates) { date, players, dates ->
			Timber.d("dateNum: $date")
			Timber.d("dates: $dates")
			players.find { it.username == dates[date].createdBy }
		}.distinctUntilChanged()

	val sabotager =
		combine(dateToShowcase, gameRepository.playersInRoom, dates) { date, players, dates ->
			players.find { it.username == dates[date].sabotagedBy }
		}.distinctUntilChanged()

	val maxTime =
		combine(gameRepository.phaseDuration.filterNotNull(), allDatesSizeFlow) { duration, size ->
			if (size > 0) {
				duration.toInt() / size
			} else {
				null
			}
		}.filterNotNull().asLiveData().distinctUntilChanged()

	val remainingTime = combine(
		remaining,
		gameRepository.phaseDuration.filterNotNull(),
		allDatesSizeFlow
	) { remainingTime, duration, size ->
		if (size > 0) {
			val timeForOne = duration.toInt() / size
			(remainingTime % timeForOne)
		} else {
			null
		}
	}.filterNotNull().asLiveData().distinctUntilChanged()

	fun getPlayerFromName(name: String?): PlayerState? {
		return gameRepository.playersInRoom.value.find { it.username == name }
	}

	fun sendMessage(winnerChoosen: WinnerChoosen) {
		viewModelScope.launch {
			redFlagsSocketService.sendMessage(winnerChoosen)
		}
	}
}

enum class ShowcaseState() {
	SHOWCASE_ONE_BY_ONE, SHOWCASE_ALL
}
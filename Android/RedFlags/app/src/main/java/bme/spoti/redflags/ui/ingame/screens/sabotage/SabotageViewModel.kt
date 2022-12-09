package bme.spoti.redflags.ui.ingame.screens.sabotage

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.data.model.DoneIndicatorInfo
import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.data.model.websocket.out.CreatedDate
import bme.spoti.redflags.data.model.websocket.out.ResumeWork
import bme.spoti.redflags.data.model.websocket.out.SabotagedDate
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.ui.base.BaseViewModel
import bme.spoti.redflags.utils.asJson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SabotageViewModel(
	private val gameRepository: GameRepository,
	private val redFlagsSocketService: RedFlagsSocketService,
) : BaseViewModel() {
	val dateToSabotage = gameRepository.dateToSabotage.stateIn(viewModelScope, SharingStarted.Eagerly,null)
	val personalData = gameRepository.personalInfo.stateIn(viewModelScope, SharingStarted.Eagerly,null)
	val cardsInHand = gameRepository.cardsInHand
	val doneCount = gameRepository.doneCount
	private val playersCount = gameRepository.playersInRoom.map { it.size }.distinctUntilChanged()
	val doneIndicatorInfo = combine(playersCount,doneCount) { all, done ->
		DoneIndicatorInfo(all-1,done)
	}.asLiveData()
	val remainingTime = gameRepository.phaseRemainingTime.asLiveData()
	val phaseDuration = gameRepository.phaseDuration.asLiveData()

	val sabotagedPerson =combine(dateToSabotage.filterNotNull(), gameRepository.playersInRoom){dateToSabotage, players->
		players.find { it.username == dateToSabotage.createdBy }
	}
	val iamSingle = combine(gameRepository.singlePlayer.filterNotNull(), gameRepository.personalInfo.filterNotNull()) {single, me ->
		single.username == me.username
	}.distinctUntilChanged()

	val lastSabotagedDate = gameRepository.lastSabotagedDate.asLiveData()
	fun sendNegativeDate(negativeAttribute : String) {
		viewModelScope.launch {
			redFlagsSocketService.sendMessage(
				SabotagedDate(
					negativeAttribute
				)
			)
		}
	}
	fun sendResumeWork() {
		viewModelScope.launch {
			redFlagsSocketService.sendMessage(
				ResumeWork()
			)
		}
	}

}
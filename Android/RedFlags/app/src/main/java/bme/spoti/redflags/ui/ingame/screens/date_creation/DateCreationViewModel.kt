package bme.spoti.redflags.ui.ingame.screens.date_creation

import androidx.compose.runtime.MutableState
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.data.model.DoneIndicatorInfo
import bme.spoti.redflags.data.model.websocket.DateInfo
import bme.spoti.redflags.data.model.websocket.PlayerState
import bme.spoti.redflags.data.model.websocket.out.CreatedDate
import bme.spoti.redflags.data.model.websocket.out.ResumeWork
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.network.ws.RedFlagsSocketServiceImpl
import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.ui.base.BaseViewModel
import bme.spoti.redflags.utils.asJson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DateCreationViewModel(
    private val gameRepository: GameRepository,
    private val redFlagsSocketService: RedFlagsSocketService
) : BaseViewModel() {

    val selectedCards = MutableStateFlow(emptyList<String>())

    private val doneCount = gameRepository.doneCount
    val cardsInHand = gameRepository.cardsInHand
    private val myPersonalInfo = gameRepository.personalInfo.filterNotNull().stateIn(viewModelScope,
        SharingStarted.Eagerly, PlayerState("","")
    )
    private val single = gameRepository.singlePlayer
    private val playersCount = gameRepository.playersInRoom.map { it.size }.distinctUntilChanged()
    val doneIndicatorInfo = combine(playersCount,doneCount) { all, done ->
        DoneIndicatorInfo(all-1,done)
    }.asLiveData()
    val isMeSingle = combine(single,myPersonalInfo){single,me->
        single?.username == me.username
    }

    val phaseDuration = gameRepository.phaseDuration.asLiveData()
    val remainingTime = gameRepository.phaseRemainingTime.asLiveData()
    val lastHandedInDate = gameRepository.lastSubmittedDate.asLiveData()
    fun sendPositiveDate(selected: MutableList<String>) {
        viewModelScope.launch {
            redFlagsSocketService.sendMessage(
                CreatedDate(
                    positiveAttributes = selected
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

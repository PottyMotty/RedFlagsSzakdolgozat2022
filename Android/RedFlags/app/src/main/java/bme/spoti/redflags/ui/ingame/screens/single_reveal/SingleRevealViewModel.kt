package bme.spoti.redflags.ui.ingame.screens.single_reveal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.ui.base.BaseViewModel
import kotlinx.coroutines.flow.filterNotNull

class SingleRevealViewModel(
    private val gameRepository: GameRepository
) : BaseViewModel(){
    val singlePlayerFlow =gameRepository.singlePlayer.filterNotNull().asLiveData()
}
package bme.spoti.redflags.ui.ingame.screens.lobby

import androidx.datastore.core.DataStore
import androidx.lifecycle.*
import bme.spoti.redflags.data.model.NearbyRoomInfo
import bme.spoti.redflags.data.model.websocket.out.StartGame
import bme.spoti.redflags.di.viewModelModule
import bme.spoti.redflags.events.UiViewModelEvent
import bme.spoti.redflags.features.nearby.NearbyEvent
import bme.spoti.redflags.features.nearby.NearbyService
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.repository.SetupRepository
import bme.spoti.redflags.repository.SetupRepositoryImpl
import bme.spoti.redflags.ui.base.BaseViewModel
import bme.spoti.redflags.utils.Resource
import bme.spoti.redflags.utils.asJson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber


class LobbyViewModel(
	private val gameRepository: GameRepository,
	private val setupRepository: SetupRepository,
	private val redFlagsSocketService: RedFlagsSocketService,
	private val nearbyService: NearbyService,
	private val clientID: String
) : BaseViewModel() {

	init {
		viewModelScope.launch {
			Timber.d("LAUNCH CUCC ELINDULT LOBBY")
			nearbyService.receivedMessages.collectLatest {
				Timber.d("MESSAGE RECEIVED FROM NEARBY")
				when (it) {
					is NearbyEvent.OnConnectionCompleted -> {
						Timber.d("Connection completed")
						passwordProtected.value?.let { isPasswordProtected ->
							nearbyService.sendData(
								it.endpoint, NearbyRoomInfo(
									roomCode.value ?: "UNKNOWN",
									playersInRoom = playersInRoom.value?.size ?: -1,
									isPasswordProtected
								)
							)
						}?: kotlin.run { throw Exception("Missing crucial information") }
					}
					NearbyEvent.AdvertisingSuccess -> {
						Timber.d("ADV SUCC")
						isSharingActive.update { true }
					}
					NearbyEvent.AdvertisingStopped -> {
						Timber.d("ADV STOP")
						isSharingActive.update { false }
					}
					is NearbyEvent.AdvertisingFailed -> {
						it.error.printStackTrace()
						isSharingActive.update { false }
					}
					else -> {
					}
				}
			}
		}
	}


	val passwordProtected = gameRepository.roomCode.filterNotNull().map {
		when (val result = setupRepository.isPasswordProtected(it)) {
			is Resource.Error -> {
				Timber.d("FAILED PASSWORD")
				return@map null
			}
			is Resource.Success -> {
				Timber.d("SUCCESS PASSWORD")
				return@map result.data
			}
		}
	}.stateIn(viewModelScope, SharingStarted.Eagerly,null)

	val roomCode = gameRepository.roomCode.asLiveData()
	val iamRoomOwner =
		gameRepository.personalInfo.map { it?.roomOwner }.filterNotNull().asLiveData()
	val playersInRoom = gameRepository.playersInRoom.asLiveData()
	val phase = gameRepository.phase.asLiveData()
	val isSharingActive = MutableStateFlow(false)
	val isQrVisible = MutableStateFlow(false)

	init {
		viewModelScope.launch {
			uiViewModelEventsOutput.collect { event ->
				when (event) {
					SharingClicked -> {
						if (isSharingActive.value) {
							nearbyService.stopAdvertising()
						} else {
							nearbyService.advertisedName = clientID
							nearbyService.startAdvertising()
						}
					}
					is QRCodeRequest -> isQrVisible.update { event.shouldShow }
					StartGameRequest -> startGame()
				}
			}
		}
	}
	fun sharingClicked(){
		if (isSharingActive.value) {
			nearbyService.stopAdvertising()
		} else {
			nearbyService.advertisedName = clientID
			nearbyService.startAdvertising()
		}
	}
	private fun startGame() {
		viewModelScope.launch {
			redFlagsSocketService.sendMessage(
				StartGame()
			)
		}
	}

}

object SharingClicked : UiViewModelEvent
data class QRCodeRequest(val shouldShow: Boolean) : UiViewModelEvent
object StartGameRequest : UiViewModelEvent

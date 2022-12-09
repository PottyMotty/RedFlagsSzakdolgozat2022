package bme.spoti.redflags.ui.setup.screens.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.data.model.websocket.out.ReconnectRequest
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.receiver.WebSocketReceiver
import bme.spoti.redflags.repository.GameRepository
import bme.spoti.redflags.repository.SetupRepository
import bme.spoti.redflags.ui.ingame.screens.reconnect.FailedReconnection
import bme.spoti.redflags.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LandingViewModel(
	private val repository: SetupRepository,
	private val gameRepository: GameRepository
) : ViewModel() {
	sealed class SetupEvent {
		object InputTooShort : SetupEvent()
		data class NavigateToGame(val roomCode: String) : SetupEvent()
		object FailedToReconnect : SetupEvent()
		data class NetworkError(val errorMsg: String) : SetupEvent()
		data class NavigateToPlayerCreation(
			val roomCode: String,
			val isPasswordProtected: Boolean
		) : SetupEvent()

		object NavigateToCreateGame : SetupEvent()
		object FailedToCheckPassword : SetupEvent()
	}

	private val _setupEvent = MutableSharedFlow<SetupEvent>(extraBufferCapacity = 1)
	val setupEvent: SharedFlow<SetupEvent> = _setupEvent.asSharedFlow()

	val roomCode = gameRepository.roomCode

	fun navigateAndValidateToPlayerCreation(roomCode: String) {
		viewModelScope.launch(Dispatchers.Main) {
			val roomCheck = repository.checkRoom(roomCode = roomCode)

			if (roomCode.length < 6) _setupEvent.emit(SetupEvent.InputTooShort)

			when (roomCheck) {
				is Resource.Success -> {
					val isPasswordProtected =
						repository.isPasswordProtected(roomCode = roomCode).data ?: run {
							_setupEvent.tryEmit(SetupEvent.FailedToCheckPassword)
							return@launch
						}
					_setupEvent.emit(
						SetupEvent.NavigateToPlayerCreation(
							roomCode,
							isPasswordProtected
						)
					)
				}
				is Resource.Error -> _setupEvent.emit(
					SetupEvent.NetworkError(
						roomCheck.message ?: "Unknown network error!"
					)
				)
			}
		}
	}

	fun navigateAndValidateToGameCreation() {
		viewModelScope.launch(Dispatchers.Main) {
			_setupEvent.emit(SetupEvent.NavigateToCreateGame)
		}
	}

	fun validateAndNavigateToGame(oldRoom: String) {
		viewModelScope.launch {
			when (repository.checkRoom(oldRoom)) {
				is Resource.Error -> _setupEvent.tryEmit(SetupEvent.FailedToReconnect)
				is Resource.Success -> _setupEvent.tryEmit(SetupEvent.NavigateToGame(oldRoom))
			}
		}
	}
}
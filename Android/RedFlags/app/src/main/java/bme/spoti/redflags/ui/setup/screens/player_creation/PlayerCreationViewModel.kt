package bme.spoti.redflags.ui.setup.screens.player_creation

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.data.model.websocket.JoinRequest
import bme.spoti.redflags.other.Constants
import bme.spoti.redflags.repository.SetupRepository
import bme.spoti.redflags.ui.base.BaseViewModel
import bme.spoti.redflags.utils.Resource
import bme.spoti.redflags.utils.UIStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random


class PlayerCreationViewModel(
    private val repository: SetupRepository
) : BaseViewModel() {
    private var imageNumber: Int = 31
    private var imageIndex = 0

    sealed class SetupEvent {
        object InputEmptyError : SetupEvent()
        data class RoomCheckFailed(val msg:String) : SetupEvent()

        data class NavigateToLobby(val roomCode: String, val joinRequest: JoinRequest) : SetupEvent()
    }


    private val _setupEvent = MutableSharedFlow<SetupEvent>()
    val setupEvent: SharedFlow<SetupEvent> = _setupEvent

    private val _imageURLID = MutableStateFlow<Int>(-1)

    val imageURLFlow = _imageURLID.filter { it>0 }.map { imageIndex -> "${Constants.HTTP_BASE_URL}/api/catImage/${imageIndex}" }.asLiveData()

    fun getNewImage() {
        viewModelScope.launch {
            imageIndex = Random.nextInt(1, imageNumber + 1)
            _imageURLID.emit(imageIndex)
        }
    }

    fun navigateAndValidateToLobby(roomCode :String,playerName: String,password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if(playerName.isEmpty()) {
                _setupEvent.emit(SetupEvent.InputEmptyError)
            }else {
                val roomCheck = repository.checkRoom(roomCode = roomCode, playerName)
                when (roomCheck) {
                    is Resource.Success -> _setupEvent.emit(SetupEvent.NavigateToLobby(roomCode,
                        JoinRequest(imageURL=imageURLFlow.value ?: "https://placekitten.com/g/500/500",username=playerName, password = password)))
                    is Resource.Error -> _setupEvent.emit(
						SetupEvent.RoomCheckFailed(
							roomCheck.message ?: "Unknown network error!"
						)
                    )
                    else->{}
                }
            }
        }
    }
}
package bme.spoti.redflags.ui.setup.screens.new_game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.data.model.CreateRoomRequest
import bme.spoti.redflags.data.model.PackMetaInfo
import bme.spoti.redflags.data.model.RoomCode
import bme.spoti.redflags.repository.SetupRepository
import bme.spoti.redflags.utils.Resource
import bme.spoti.redflags.utils.UIStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class NewGameViewModel(
    private val repository: SetupRepository
): ViewModel() {
    sealed class SetupEvent{
        data class GetPacksEvent(val packs: List<PackMetaInfo>) : SetupEvent()
        data class GetPacksErrorEvent(val error: String) : SetupEvent()
        object GetPacksLoadingEvent : SetupEvent()
        object GetPacksEmptyEvent : SetupEvent()
        data class NavigateToPlayerCreation(val roomCode: RoomCode) : SetupEvent()
        data class NetworkError(val error: String) : SetupEvent()
    }
    private val _packs = MutableStateFlow<UIStateOf<List<PackMetaInfo>>>(UIStateOf.Loading)
    val packs: StateFlow<UIStateOf<List<PackMetaInfo>>> = _packs

    private val _setupEvent = MutableSharedFlow<SetupEvent>()
    val setupEvent: SharedFlow<SetupEvent> = _setupEvent

    fun getPacks(){
        _packs.update { UIStateOf.Loading }
        viewModelScope.launch {
            val result = repository.getPacks()
            if(result is Resource.Success){
                if(result.data != null){
                    _packs.update { UIStateOf.Success(result.data)}
                }else {
                    _packs.update {
                        UIStateOf.Failure(
                            message = "Failed to load packs from network",
                            null
                        )
                    }
                }
            }else{
                _packs.update { UIStateOf.Failure(message = "Failed to load packs from network",null) }
            }
        }
    }

    fun navigateAndValidateToCreatePlayer(createRoomRequest: CreateRoomRequest, onSuccess: (String)->Unit){
        viewModelScope.launch(Dispatchers.Main) {
            val roomCode = repository.createRoom(createRoomRequest)
            when (roomCode) {
                is Resource.Success -> {
                    roomCode.data?.let {
                        onSuccess(roomCode.data.string)
                    }
                }
                is Resource.Error -> {

                }
            }
        }
    }
}
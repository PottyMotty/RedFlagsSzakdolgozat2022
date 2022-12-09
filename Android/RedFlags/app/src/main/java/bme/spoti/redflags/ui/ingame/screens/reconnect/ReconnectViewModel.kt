package bme.spoti.redflags.ui.ingame.screens.reconnect

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.websocket.out.JoinRoomHandshake
import bme.spoti.redflags.data.model.websocket.out.ReconnectRequest
import bme.spoti.redflags.events.UIEvent
import bme.spoti.redflags.network.ws.RedFlagsSocketService
import bme.spoti.redflags.receiver.WebSocketReceiver
import bme.spoti.redflags.utils.Resource
import bme.spoti.redflags.utils.asJson
import bme.spoti.redflags.utils.roomId
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ReconnectViewModel(
	private val redFlagsSocketService: RedFlagsSocketService,
	private val preferencesDataStore: DataStore<Preferences>,
	private val webSocketReceiver: WebSocketReceiver,
) : ViewModel() {

	val eventBus :MutableSharedFlow<UIEvent> = MutableSharedFlow()
	val connectionState = redFlagsSocketService.socketStateFlow.asLiveData()
	fun reconnect() {
		viewModelScope.launch {
			val roomCode = preferencesDataStore.roomId()
			when(val result =redFlagsSocketService.initSession(roomCode)){
				is Resource.Error -> {
					eventBus.tryEmit(FailedReconnection(result.message))
					Timber.d("Error websockets: ${result.message}")
				}
				is Resource.Success -> {
					webSocketReceiver.listenToMessages()
					Timber.d("Connection opened")
					redFlagsSocketService.sendMessage(ReconnectRequest())
				}
			}
		}
	}
}

data class FailedReconnection(val message: String?) : UIEvent
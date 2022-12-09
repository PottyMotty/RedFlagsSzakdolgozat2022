package bme.spoti.redflags.ui.setup.screens.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.spoti.redflags.data.model.NearbyRoomInfo
import bme.spoti.redflags.features.nearby.NearbyEvent
import bme.spoti.redflags.features.nearby.NearbyService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class NearbyRoomsViewModel(
	private val nearbyService: NearbyService,
	private val clientID: String
) : ViewModel(){
	val nearbyRooms = MutableStateFlow<Map<String, NearbyRoomInfo>>(mapOf())

	init {
		viewModelScope.launch {
			Timber.d("LAUNCH ELINDULT")
			nearbyService.receivedMessages.collect {event->
				when(event){
					is NearbyEvent.MessageReceived<*> -> {
						if(event.message is NearbyRoomInfo){
							nearbyRooms.update { nearbyRooms.value.plus(Pair(event.endpoint,event.message)) }
							Timber.d("ROOM ADDED")
						}
					}
					is NearbyEvent.OnDisconnected -> {
						Timber.d("ROOM SHOULD DELETE")
						nearbyRooms.update { it.minus(event.endpoint) }
					}
					else->{
					}
				}
			}
		}
	}

	fun startListening(){
		nearbyService.advertisedName = clientID
		nearbyService.startDiscovery()
	}
	fun stopListening(){
		nearbyService.stopDiscovery()
	}
}
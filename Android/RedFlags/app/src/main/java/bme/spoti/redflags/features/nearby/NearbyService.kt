package bme.spoti.redflags.features.nearby

import bme.spoti.redflags.data.model.NearbyRoomInfo
import kotlinx.coroutines.flow.MutableSharedFlow

interface NearbyService {
	var advertisedName: String
	val receivedMessages: MutableSharedFlow<NearbyEvent>
	fun startAdvertising()
	fun stopAdvertising()
	fun startDiscovery()
	fun stopDiscovery()
	fun sendData(endpoint: String,roomInfo: NearbyRoomInfo)
}
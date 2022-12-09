package bme.spoti.redflags.network.ws

import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.other.Constants
import bme.spoti.redflags.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface RedFlagsSocketService {
	suspend fun initSession(
		roomCode: String
	): Resource<Unit>
	val socketStateFlow : MutableStateFlow<SocketState>

	suspend fun sendMessage(message: WebsocketRequestModel)

	fun observeMessages(): Flow<WebsocketRequestModel>

	suspend fun closeSession()

	sealed class Endpoints(val url: String) {
		object ChatSocket: Endpoints(if(Constants.USE_LOCALHOST) {Constants.WS_BASE_URL_LOCALHOST} else { Constants.WS_BASE_URL} +"/ws/game")
	}
}
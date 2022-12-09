package bme.spoti.redflags.network.ws

import bme.spoti.redflags.data.model.websocket.WebsocketRequestModel
import bme.spoti.redflags.utils.Resource
import bme.spoti.redflags.utils.asJson
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.plugins.websocket.cio.*
import io.ktor.http.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import timber.log.Timber

class RedFlagsSocketServiceImpl(
	private val client: HttpClient,
	private val gson: Gson
) : RedFlagsSocketService {

	private var socket: WebSocketSession? = null
	override val socketStateFlow = MutableStateFlow(SocketState.Disconnected)

	override suspend fun initSession(roomCode: String): Resource<Unit> {
		return try {
			Timber.d("WEBSOCKET URL: ${RedFlagsSocketService.Endpoints.ChatSocket.url}/$roomCode")
			socket = client.webSocketSession(urlString = "${RedFlagsSocketService.Endpoints.ChatSocket.url}/$roomCode")
			if (socket?.isActive == true) {
				socketStateFlow.update { SocketState.Connected }
				Resource.Success(Unit)
			} else Resource.Error("Couldn't establish a connection.")
		} catch (e: Exception) {
			e.printStackTrace()
			socketStateFlow.update { SocketState.FailedToConnect }
			Resource.Error(e.localizedMessage ?: "Unknown error")
		}
	}

	override suspend fun sendMessage(message: WebsocketRequestModel) {
		try {
			socket?.send(Frame.Text(message.asJson()))
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	override fun observeMessages(): Flow<WebsocketRequestModel> {
		return try {
			socket?.incoming
				?.receiveAsFlow()
				?.filter { it is Frame.Text }
				?.map {
					val json = (it as? Frame.Text)?.readText() ?: ""
					Timber.d(json)
					val messageDto = gson.fromJson(json,WebsocketRequestModel::class.java)
					messageDto
				} ?: flow { }
		} catch (e: Exception) {
			socketStateFlow.update { SocketState.ConnectionLost }
			e.printStackTrace()
			flow { }
		}
	}

	override suspend fun closeSession() {
		socketStateFlow.update { SocketState.ConnectionLost }
		socket?.close()
	}
}

enum class SocketState(){
	Connected, Disconnected, ConnectionLost, FailedToConnect
}
package bme.spoti.redflags.features.nearby

import android.content.Context
import bme.spoti.redflags.data.model.NearbyRoomInfo
import bme.spoti.redflags.utils.asJson
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class NearbyServiceImpl(
	private val context: Context
) : NearbyService{
	 companion object{
		 private val STRATEGY = Strategy.P2P_STAR
		 private val SERVICE_ID = "bme.spoti.redflags"
	 }
	override lateinit var advertisedName : String

	override val receivedMessages: MutableSharedFlow<NearbyEvent> = MutableSharedFlow(replay = 0,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
		extraBufferCapacity = 1)
	override fun startAdvertising() {
		val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
		Nearby.getConnectionsClient(context)
			.startAdvertising(
				advertisedName, SERVICE_ID, connectionLifecycleCallback, advertisingOptions
			)
			.addOnSuccessListener{
				Timber.d("ADVERTISING SUCCESS")
				receivedMessages.tryEmit(NearbyEvent.AdvertisingSuccess)

			}
			.addOnFailureListener{
				Timber.d("ADVERTISING FAILED")
				receivedMessages.tryEmit(NearbyEvent.AdvertisingFailed(it))
			}
	}

	override fun stopAdvertising() {
		Nearby.getConnectionsClient(context).stopAdvertising()
		Nearby.getConnectionsClient(context).stopAllEndpoints()
		receivedMessages.tryEmit(NearbyEvent.AdvertisingStopped)
	}

	override fun startDiscovery() {
		val discoveryOptions =
			DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
		Nearby.getConnectionsClient(context)
			.startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
			.addOnSuccessListener {
				Timber.d("DISCOVERY STARTED SUCCESSFULLY")
			}
			.addOnFailureListener {
				Timber.d("DISCOVERY FAILED SUCCESSFULLY")
			}
	}

	override fun stopDiscovery() {
		Nearby.getConnectionsClient(context).stopDiscovery()
	}

	override fun sendData(endpoint: String,roomInfo: NearbyRoomInfo) {
		val stringForm = roomInfo.asJson()
		val payload = Payload.fromBytes(stringForm.toByteArray())
		Nearby.getConnectionsClient(context).sendPayload(endpoint,payload)
	}

	private val endpointDiscoveryCallback: EndpointDiscoveryCallback =
		object : EndpointDiscoveryCallback() {
			override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
				// An endpoint was found. We request a connection to it.
				Nearby.getConnectionsClient(context)
					.requestConnection(advertisedName, endpointId, connectionLifecycleCallback)
					.addOnSuccessListener{
						Timber.d("ENDPOINT FOUND SUCCESSFULLY $endpointId")
					}
					.addOnFailureListener{
						it.printStackTrace()
						Timber.d("ENDPOINT NOT FOUND FAILED SUCCESSFULLY $advertisedName")
					}
			}

			override fun onEndpointLost(endpointId: String) {
				Timber.d("ENDPOINT VANISHED")

			}
		}
	private val connectionLifecycleCallback: ConnectionLifecycleCallback =
		object : ConnectionLifecycleCallback() {
			override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
				// Automatically accept the connection on both sides.
				Nearby.getConnectionsClient(context).acceptConnection(endpointId, roomInfoBytePayloadListener)
			}

			override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
				when (result.status.statusCode) {
					ConnectionsStatusCodes.STATUS_OK -> {
						Timber.d("CONNECTION OK")
						receivedMessages.tryEmit(NearbyEvent.OnConnectionCompleted(endpointId))
					}
					ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
						Timber.d("CONNECTION REJECTED")
					}
					ConnectionsStatusCodes.STATUS_ERROR -> {
						Timber.d("CONNECTION ERROR")
					}
					else -> {}
				}
			}

			override fun onDisconnected(endpointId: String) {
				Timber.d("CONNECTION DISCONNECTED")
				receivedMessages.tryEmit(NearbyEvent.OnDisconnected(endpointId))
				// We've been disconnected from this endpoint. No more data can be
				// sent or received.
			}
		}

	private val roomInfoBytePayloadListener = object: PayloadCallback() {
		override fun onPayloadReceived(endpointId: String, payload: Payload) {
			// This always gets the full data of the payload. Is null if it's not a BYTES payload.
			if (payload.type == Payload.Type.BYTES) {
				val receivedBytes = payload.asBytes() ?: ByteArray(0)
				val stringForm = String(receivedBytes)
				val roomInfo = Gson().fromJson(stringForm, NearbyRoomInfo::class.java)
				Timber.d("RECEIVED: $roomInfo")
				receivedMessages.tryEmit(NearbyEvent.MessageReceived(endpointId,roomInfo))
			}
		}

		override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
			// Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
			// after the call to onPayloadReceived().
		}
	}
}

sealed class NearbyEvent{
	data class OnDisconnected(val endpoint: String) : NearbyEvent()
	object AdvertisingSuccess : NearbyEvent()
	object AdvertisingStopped : NearbyEvent()
	data class AdvertisingFailed(val error: Throwable) : NearbyEvent()
	data class OnConnectionCompleted(val endpoint: String) : NearbyEvent()
	data class MessageReceived<T>(val endpoint: String, val message: T) : NearbyEvent()
}
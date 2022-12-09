package bme.spoti.redflags.data.model

data class NearbyRoomInfo(
	val roomCode: String,
	val playersInRoom: Int,
	val isPasswordProtected: Boolean
)
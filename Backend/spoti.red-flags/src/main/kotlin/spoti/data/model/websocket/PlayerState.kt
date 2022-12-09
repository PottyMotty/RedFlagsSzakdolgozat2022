package spoti.data.model.websocket

data class PlayerState(
    val username: String,
    val imageURL: String,
    val roomOwner: Boolean = false,
    val isConnected: Boolean = true
) {
}
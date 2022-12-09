package bme.spoti.redflags.data.model

data class CreateRoomRequest(
    val numRounds: Int,
    val packs: List<Int>,
    val password: String="",
)
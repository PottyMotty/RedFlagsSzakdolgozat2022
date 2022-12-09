package bme.spoti.redflags.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CardData(
    val type : String,
    val content : String
)

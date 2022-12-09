package bme.spoti.redflags.data.model.websocket

import kotlinx.serialization.Serializable

@Serializable
data class DateInfo(val positiveAttributes: List<String>, val negativeAttribute: String?=null, val createdBy: String, val sabotagedBy: String?=null)
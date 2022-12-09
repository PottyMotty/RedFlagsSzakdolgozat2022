package spoti.data.model.websocket

data class DateInfo(val positiveAttributes: List<String>, var negativeAttribute: String?=null, val createdBy: String, var sabotagedBy: String?=null)
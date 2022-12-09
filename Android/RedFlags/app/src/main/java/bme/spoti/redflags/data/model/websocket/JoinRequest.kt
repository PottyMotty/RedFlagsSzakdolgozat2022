package bme.spoti.redflags.data.model.websocket

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JoinRequest(val username: String, val imageURL: String, val password:String) : Parcelable
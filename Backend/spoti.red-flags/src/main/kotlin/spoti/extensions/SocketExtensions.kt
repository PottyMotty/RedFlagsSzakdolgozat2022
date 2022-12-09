package spoti.extensions

import com.google.gson.Gson
import io.ktor.websocket.*
import spoti.data.model.BasicResponse
import spoti.data.model.websocket.out.ErrorAnnouncement
import spoti.other.extensions.asJson

suspend fun WebSocketSession.sendError(message: String){
    val error = ErrorAnnouncement(
        message
    )
    this.send(error.asJson())
}
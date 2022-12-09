package bme.spoti.redflags.network.http.cats

import bme.spoti.redflags.data.model.*
import retrofit2.http.*

interface SetupApi {
    @GET("/api/images")
    suspend fun getImageData() : ImageStoreData

    @GET("/api/checkRoom/{roomCode}")
    suspend fun checkRoom(@Path("roomCode") roomCode: String, @Query("username") username : String?): BasicResponse

    @GET("/api/isPasswordProtected/{roomCode}")
    suspend fun isPasswordProtected(@Path("roomCode") roomCode: String): Boolean

    @GET("/api/packs")
    suspend fun getPacks() : List<PackMetaInfo>

    @POST("/api/createRoom")
    suspend fun createRoom(@Body createRoomRequest: CreateRoomRequest) : RoomCode
}

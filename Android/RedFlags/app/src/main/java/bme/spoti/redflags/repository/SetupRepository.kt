package bme.spoti.redflags.repository

import bme.spoti.redflags.data.model.*
import bme.spoti.redflags.utils.Resource

interface SetupRepository {

    suspend fun getImageData() : Resource<ImageStoreData>

    suspend fun checkRoom(roomCode: String, username: String?=null) : Resource<BasicResponse>
    suspend fun isPasswordProtected(roomCode: String) : Resource<Boolean>

    suspend fun getPacks(): Resource<List<PackMetaInfo>>

    suspend fun createRoom(createRoomRequest: CreateRoomRequest): Resource<RoomCode>
}
package bme.spoti.redflags.repository

import android.content.Context
import bme.spoti.redflags.R
import bme.spoti.redflags.data.model.*
import bme.spoti.redflags.network.http.cats.SetupApi
import bme.spoti.redflags.utils.Resource
import bme.spoti.redflags.utils.checkForInternetConnection
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import retrofit2.HttpException
import java.io.IOException

class SetupRepositoryImpl(
    private val setupApi: SetupApi,
    private val context: Context
) : SetupRepository {
    override suspend fun getImageData(): Resource<ImageStoreData> {
        if (!context.checkForInternetConnection()) {
            return Resource.Error(context.getString(R.string.error_internet_turned_off))
        }
        val response = try {
            setupApi.getImageData()
        } catch (e: HttpException) {
            return Resource.Error(context.getString(R.string.error_http))
        } catch (e: IOException) {
            return Resource.Error(context.getString(R.string.check_internet_connection))
        }

        return Resource.Success(response)
    }

    override suspend fun checkRoom(roomCode: String, username: String?): Resource<BasicResponse> {
        return simpleAPIRequest<BasicResponse>{setupApi.checkRoom(roomCode,username)}
    }

    override suspend fun isPasswordProtected(roomCode: String): Resource<Boolean> {
        return simpleAPIRequest { setupApi.isPasswordProtected(roomCode) }
    }

    private suspend fun <T> simpleAPIRequest(valueFun: suspend ()-> T) : Resource<T>{
        if (!context.checkForInternetConnection()) {
            return Resource.Error(context.getString(R.string.error_internet_turned_off))
        }
        val response = try {
            valueFun.invoke()
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()
            val gson = Gson()
            val adapter: TypeAdapter<BasicResponse> = gson.getAdapter(BasicResponse::class.java)
            try {
                val error: BasicResponse? = adapter.fromJson(body?.string())
                return Resource.Error(error?.msg ?: "Unknown")
            } catch (e: IOException) {
                return Resource.Error("Error parsing error")
            }
        } catch (e: IOException) {
            return Resource.Error(context.getString(R.string.check_internet_connection))
        }
        return Resource.Success(response)
    }
    override suspend fun getPacks(): Resource<List<PackMetaInfo>> {

       return simpleAPIRequest<List<PackMetaInfo>> { setupApi.getPacks() }
    }

    override suspend fun createRoom(createRoomRequest: CreateRoomRequest): Resource<RoomCode> {
        return simpleAPIRequest<RoomCode>{setupApi.createRoom(createRoomRequest)}
    }

}
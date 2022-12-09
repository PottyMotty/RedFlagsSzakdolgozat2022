package bme.spoti.redflags.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

val Context.dataStore by preferencesDataStore("settings")

suspend fun DataStore<Preferences>.clientID(): String{
    val clientIdKey= stringPreferencesKey("clientID")
    val preferences =data.first()
    val clientIdExists =preferences[clientIdKey] !=null
    return if(clientIdExists){
        preferences[clientIdKey] ?:""
    }else{
        val newClientID =UUID.randomUUID().toString()
        edit{ settings->
            settings[clientIdKey] =newClientID
        }
        newClientID
    }
}
suspend fun DataStore<Preferences>.saveRoomCode(roomCode: String){
    val roomCodeKey= stringPreferencesKey("roomCode")
    edit{ settings->
        settings[roomCodeKey] = roomCode
    }
}
suspend fun DataStore<Preferences>.roomId(): String{
    val roomCodeKey= stringPreferencesKey("roomCode")
    val preferences =data.first()
    return preferences[roomCodeKey] ?:""
}

fun DataStore<Preferences>.roomIdFlow(): Flow<String?> {
    val roomCodeKey= stringPreferencesKey("roomCode")
    return data.map { prefs->
        prefs[roomCodeKey]
    }
}

suspend fun DataStore<Preferences>.deleteRoom() {
    val roomCodeKey= stringPreferencesKey("roomCode")
    edit {
        it.remove(roomCodeKey)
    }
}
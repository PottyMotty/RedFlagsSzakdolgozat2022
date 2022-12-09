package bme.spoti.redflags.utils

import com.google.gson.Gson

fun Any.asJson() : String{
	return Gson().toJson(this)
}
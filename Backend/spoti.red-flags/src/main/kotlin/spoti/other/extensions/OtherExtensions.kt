package spoti.other.extensions

import com.google.gson.Gson
import spoti.other.Phase

val Int.seconds : Long
    get() = this*1000L

fun <T> List<T>.rotateLeft(n: Int) = drop(n) + take(n)

fun <T> Pair<T,T>.isTheSame(predicate: T.()-> Any) : Boolean{
    return this.first.predicate() == this.second.predicate()
}

fun Any.asJson() : String{
    return Gson().toJson(this)
}
fun Phase.isCraftingPhase(): Boolean {
    return this == Phase.DATE_CRAFTING || this == Phase.SABOTAGE
}

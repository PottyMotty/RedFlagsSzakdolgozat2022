package bme.spoti.redflags.utils

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}

sealed class UIStateOf<out T>{
    data class Success<out R>(val data: R) : UIStateOf<R>()
    object Loading : UIStateOf<Nothing>()
    data class Failure(val message: String?, val throwable: Throwable?) : UIStateOf<Nothing>()
}

inline fun <reified T> UIStateOf<T>.doIfFailure(callback: (error: String?, throwable: Throwable?) -> Unit) {
    if (this is UIStateOf.Failure) {
        callback(message, throwable)
    }
}
inline fun <reified T> UIStateOf<T>.doWhileLoading(callback: () -> Unit) {
    if (this is UIStateOf.Loading) {
        callback()
    }
}
inline fun <reified T> UIStateOf<T>.doIfSuccess(callback: (value: T) -> Unit) {
    if (this is UIStateOf.Success) {
        callback(data)
    }
}
package dev.dhiren.common.resource.model

sealed class DataResource<T>(
    val data: T? = null,
    val error: Any? = null
) {
    class Success<T>(data: T) : DataResource<T>(data)
    class Error<T>(error: Any, data: T? = null) : DataResource<T>(data, error)
    class Loading<T>(data: T? = null) : DataResource<T>(data)

    companion object {
        fun <T> success(data: T): DataResource<T> = Success(data)
        fun <T> error(error: Any, data: T? = null): DataResource<T> = Error(error, data)
        fun <T> loading(data: T? = null): DataResource<T> = Loading(data)
    }
}

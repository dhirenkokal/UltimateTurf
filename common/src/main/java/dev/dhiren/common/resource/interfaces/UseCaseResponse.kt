package dev.dhiren.common.resource.interfaces

interface UseCaseResponse<T> {
    fun onLoading()
    fun onSuccess(data: T)
    fun onError(error: Any)
}

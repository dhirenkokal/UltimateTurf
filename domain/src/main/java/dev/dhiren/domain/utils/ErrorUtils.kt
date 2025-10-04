package dev.dhiren.domain.utils

object ErrorUtils {
    fun traceErrorException(throwable: Throwable): String {
        return throwable.message ?: "Unknown error occurred"
    }
}

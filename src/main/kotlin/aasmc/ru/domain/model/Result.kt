package aasmc.ru.domain.model

sealed class Result<out T> {

    data class Failure(val cause: Throwable) : Result<Nothing>()

    data class Success<T>(val data: T) : Result<T>()

}

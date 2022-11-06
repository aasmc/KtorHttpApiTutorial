package aasmc.ru.domain.model.exceptions

class OperationFailedException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {
}
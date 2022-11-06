package aasmc.ru.domain.model.exceptions

class TransactionRollbackFailedException(
    override val message: String,
    cause: Throwable? = null
): Exception(message, cause) {
}
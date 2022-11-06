package aasmc.ru.domain.model.exceptions

class ItemNotFoundException(
    override val message: String,
    override val cause: Throwable? = null
): Exception(message, cause) {
}
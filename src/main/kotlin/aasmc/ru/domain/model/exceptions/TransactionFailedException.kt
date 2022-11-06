package aasmc.ru.domain.model.exceptions

class TransactionFailedException(
    override val message: String
) : Exception(message)
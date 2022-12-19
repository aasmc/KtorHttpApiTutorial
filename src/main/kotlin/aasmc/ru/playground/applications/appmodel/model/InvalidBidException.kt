package aasmc.ru.playground.applications.appmodel.model

import javax.ejb.ApplicationException

@ApplicationException(rollback = true)
class InvalidBidException @JvmOverloads constructor(
    message: String? = null,
    throwable: Throwable? = null
) : Exception(message, throwable)
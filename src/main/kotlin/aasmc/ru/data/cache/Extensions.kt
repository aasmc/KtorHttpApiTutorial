package aasmc.ru.data.cache

import org.hibernate.Session

suspend fun <T> Session.withTransaction(block: suspend Session.() -> T) : T {
    beginTransaction()
    val result = block()
    transaction.commit()
    close()
    return result
}
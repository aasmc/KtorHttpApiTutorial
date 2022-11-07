package aasmc.ru.data.cache

import aasmc.ru.domain.model.exceptions.OperationFailedException
import aasmc.ru.domain.model.Result
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityTransaction
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOGGER: Logger = LoggerFactory.getLogger("Application Logger")

suspend fun <T> Session.withTransaction(block: suspend Session.() -> T): T {
    beginTransaction()
    val result = block()
    transaction.commit()
    close()
    return result
}

suspend fun <T> SessionFactory.withSession(block: suspend Session.() -> T): Result<T> {
    var session: Session? = null
    var transaction: Transaction? = null
    try {
        session = openSession()
        transaction = session.beginTransaction()
        val data = block(session)
        if (!transaction.rollbackOnly) {
            transaction.commit()
        } else {
            try {
                transaction.rollback()
            } catch (e: Exception) {
                LOGGER.error("Rollback failure", e)
            }
        }
        return Result.Success(data)
    } catch (t: Throwable) {
        if (transaction != null && transaction.isActive) {
            try {
                transaction.rollback()
            } catch (e: Exception) {
                LOGGER.error("Rollback failure", e)
            }
        }
        return Result.Failure(
            OperationFailedException(
                message = "Operation with the database failed",
                cause = t.cause
            )
        )
    } finally {
        session?.close()
    }
}

suspend fun <T> EntityManagerFactory.withEntityManager(block: suspend EntityManager.() -> T): Result<T> {
    var entityManager: EntityManager? = null
    var txn: EntityTransaction? = null
    try {
        entityManager = createEntityManager()
        txn = entityManager.transaction
        txn.begin()
        val result = block(entityManager)
        if (!txn.rollbackOnly) {
            txn.commit()
        } else {
            try {
                txn.rollback()
            } catch (e: Exception) {
                LOGGER.error("Rollback failure", e)
            }
        }
        return Result.Success(result)
    } catch (t: Throwable) {
        if (txn != null && txn.isActive) {
            try {
                txn.rollback()
            } catch (e: Exception) {
                LOGGER.error("Rollback failure", e)
            }
        }
        return Result.Failure(
            OperationFailedException(
                message = "Operation with the database failed",
                cause = t.cause
            )
        )
    } finally {
        entityManager?.close()
    }
}























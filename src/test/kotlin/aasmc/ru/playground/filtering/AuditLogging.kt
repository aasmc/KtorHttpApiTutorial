package aasmc.ru.playground.filtering

import aasmc.ru.data.cache.hibernateproviders.interfaces.InterceptorProvider
import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.data.cache.withSession
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.filtering.interceptor.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hibernate.Interceptor
import org.hibernate.Session
import org.hibernate.engine.spi.SessionImplementor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuditLogging : AbstractTest(
    entityProvider = {
        arrayOf(
            AuditLogRecord::class.java,
            Item::class.java,
            User::class.java
        )
    }
) {
    @Test
    fun writeAuditLog() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val currentUser = User(username = "johndoe")
            persist(currentUser)
            currentUser
        }
        assertTrue(idRes is Result.Success)
        val currentUserId = idRes.data.id ?: 0

        val options = hashMapOf(
            org.hibernate.cfg.AvailableSettings.SESSION_SCOPED_INTERCEPTOR
            to
            AuditLogInterceptor::class.java.name
        )
        val itemRes = entityManagerFactory.withEntityManager(options) {
            val session = unwrap(Session::class.java)
            val interceptor = (session as SessionImplementor).interceptor as AuditLogInterceptor
            interceptor.currentSession = session
            interceptor.currentUserId = currentUserId

            val item = Item(name = "Foo")
            persist(item)
            item
        }

        assertTrue(itemRes is Result.Success)
        val item = itemRes.data

        val auditRes = sessionFactory.withSession {
            val logs = createQuery(
                "select lr from AuditLogRecord lr",
                AuditLogRecord::class.java
            ).resultList

            assertEquals(1, logs.size)
            assertEquals("insert", logs[0].message)
            assertEquals(Item::class.java, logs[0].entityClass)
            assertEquals(currentUserId, logs[0].userId)
            assertEquals(item.id, logs[0].entityId)
            createQuery("delete AuditLogRecord", AuditLogRecord::class.java).executeUpdate()
        }
        assertTrue(auditRes is Result.Success)

    }
}

















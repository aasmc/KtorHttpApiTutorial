package aasmc.ru.data.cache.daos.impl

import aasmc.ru.data.cache.HibernateSessionFactory
import aasmc.ru.data.cache.daos.interfaces.SecurityDAO
import aasmc.ru.data.cache.models.CachedUser
import aasmc.ru.data.cache.withTransaction
import org.hibernate.SessionFactory

class HibernateSecurityDao(
    private val sessionFactory: SessionFactory = HibernateSessionFactory.sessionFactory
) : SecurityDAO {

    override suspend fun getUserByUsername(username: String): CachedUser? {
        val session = sessionFactory.openSession()
        val result = session.withTransaction {
            val query = createQuery(
                "select u from CachedUser u where u.username = :userName",
                CachedUser::class.java
            ).setParameter("userName", username)
            query.singleResultOrNull
        }
        return result
    }

    override suspend fun insertUser(user: CachedUser): Boolean {
        val session = sessionFactory.openSession()
        val inserted = session.withTransaction {
            try {
                persist(user)
                true
            } catch (e: Exception) {
                false
            }
        }
        return inserted
    }

    override suspend fun userAlreadyExists(username: String): Boolean {
        val session = sessionFactory.openSession()
        val exists = session.withTransaction {
            val query = createQuery(
                "select u from CachedUser u where u.username = :userName",
                CachedUser::class.java
            ).setParameter("userName", username)
            query.singleResultOrNull != null
        }
        return exists
    }
}
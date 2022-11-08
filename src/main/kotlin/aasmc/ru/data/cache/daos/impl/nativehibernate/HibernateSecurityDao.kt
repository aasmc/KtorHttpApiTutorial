package aasmc.ru.data.cache.daos.impl.nativehibernate

import aasmc.ru.data.cache.hibernateproviders.HibernateFactory
import aasmc.ru.data.cache.daos.interfaces.SecurityDAO
import aasmc.ru.data.cache.models.CachedUser
import aasmc.ru.data.cache.withSession
import aasmc.ru.domain.model.Result
import org.hibernate.SessionFactory

class HibernateSecurityDao(
    private val sessionFactory: SessionFactory = HibernateFactory.sessionFactory
) : SecurityDAO {

    override suspend fun getUserByUsername(username: String): Result<CachedUser?> {
        val result = sessionFactory.withSession {
            val query = createQuery(
                "select u from CachedUser u where u.username = :userName",
                CachedUser::class.java
            ).setParameter("userName", username)
            query.singleResultOrNull
        }
        return result
    }

    override suspend fun insertUser(user: CachedUser): Result<Unit> {
        val result = sessionFactory.withSession {
            persist(user)
        }
        return result
    }

    override suspend fun userAlreadyExists(username: String): Result<Boolean> {
        val result = sessionFactory.withSession {
            val query = createQuery(
                "select u from CachedUser u where u.username = :userName",
                CachedUser::class.java
            ).setParameter("userName", username)
            query.singleResultOrNull != null
        }
        return result
    }
}
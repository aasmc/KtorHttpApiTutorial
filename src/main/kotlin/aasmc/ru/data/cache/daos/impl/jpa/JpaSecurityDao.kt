package aasmc.ru.data.cache.daos.impl.jpa

import aasmc.ru.data.cache.daos.interfaces.SecurityDAO
import aasmc.ru.data.cache.models.CachedUser
import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.NoResultException
import jakarta.persistence.PersistenceException

class JpaSecurityDao(
    private val entityManagerFactory: EntityManagerFactory
) : SecurityDAO {

    override suspend fun getUserByUsername(username: String): Result<CachedUser?> {
        return entityManagerFactory.withEntityManager {
            val query =
                createQuery(
                    "select u from CachedUser u where u.username = :username",
                    CachedUser::class.java
                ).setParameter("username", username)
            query.resultList.firstNotNullOfOrNull { it }
        }
    }

    override suspend fun insertUser(user: CachedUser): Result<Unit> {
        return entityManagerFactory.withEntityManager {
            persist(user)
        }
    }

    override suspend fun userAlreadyExists(username: String): Result<Boolean> {
        return entityManagerFactory.withEntityManager {
            val query = createQuery(
                "select u from CachedUser u where u.username =:username",
                CachedUser::class.java
            ).setParameter("username", username)
            query.resultList.firstNotNullOfOrNull { it } != null
        }
    }
}

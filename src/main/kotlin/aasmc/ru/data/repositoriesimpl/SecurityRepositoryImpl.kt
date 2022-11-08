package aasmc.ru.data.repositoriesimpl

import aasmc.ru.data.cache.daos.impl.jpa.JpaSecurityDao
import aasmc.ru.data.cache.daos.impl.nativehibernate.HibernateSecurityDao
import aasmc.ru.data.cache.daos.interfaces.SecurityDAO
import aasmc.ru.data.cache.hibernateproviders.HibernateFactory
import aasmc.ru.data.cache.models.mappers.UsersMapper
import aasmc.ru.domain.model.Result
import aasmc.ru.domain.model.User
import aasmc.ru.domain.repositories.SecurityRepository
import io.ktor.server.config.*

class SecurityRepositoryImpl(
    private val config: ApplicationConfig,
    private val securityDAO: SecurityDAO = JpaSecurityDao(HibernateFactory.createEntityManagerFactory(config)),
    private val usersMapper: UsersMapper = UsersMapper()
) : SecurityRepository {

    override suspend fun getUserByUsername(username: String): Result<User?> {
        return when(val result = securityDAO.getUserByUsername(username)) {
            is Result.Failure -> result
            is Result.Success -> Result.Success(result.data?.let { usersMapper.mapToDomain(it) })
        }
    }

    override suspend fun insertUser(user: User): Result<Unit> =
        securityDAO.insertUser(usersMapper.mapToEntity(user))

    override suspend fun userAlreadyExists(username: String): Result<Boolean> =
        securityDAO.userAlreadyExists(username)
}
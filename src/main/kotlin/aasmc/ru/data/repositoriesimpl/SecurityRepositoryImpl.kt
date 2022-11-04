package aasmc.ru.data.repositoriesimpl

import aasmc.ru.data.cache.daos.impl.HibernateSecurityDao
import aasmc.ru.data.cache.daos.interfaces.SecurityDAO
import aasmc.ru.data.cache.models.mappers.UsersMapper
import aasmc.ru.domain.model.User
import aasmc.ru.domain.repositories.SecurityRepository

class SecurityRepositoryImpl(
    private val securityDAO: SecurityDAO = HibernateSecurityDao(),
    private val usersMapper: UsersMapper = UsersMapper()
) : SecurityRepository {

    override suspend fun getUserByUsername(username: String): User? =
        securityDAO.getUserByUsername(username)?.let {
            usersMapper.mapToDomain(it)
        }

    override suspend fun insertUser(user: User): Boolean =
        securityDAO.insertUser(usersMapper.mapToEntity(user))

    override suspend fun userAlreadyExists(username: String): Boolean =
        securityDAO.userAlreadyExists(username)
}
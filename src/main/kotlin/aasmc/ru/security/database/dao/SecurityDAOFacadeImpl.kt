package aasmc.ru.security.database.dao

import aasmc.ru.database.DatabaseFactory.dbQuery
import aasmc.ru.security.model.user.User
import aasmc.ru.security.model.user.UsersEntity
import aasmc.ru.security.model.user.UsersTable

class SecurityDAOFacadeImpl : SecurityDAOFacade {
    override suspend fun getUserByUsername(username: String): User? = dbQuery {
        val entity = UsersEntity.find { UsersTable.username eq username }.singleOrNull()
            ?: return@dbQuery null
        return@dbQuery User(
            id = entity.id.value,
            username = entity.username,
            password = entity.password,
            salt = entity.salt
        )
    }

    override suspend fun insertUser(user: User): Boolean = dbQuery {
        return@dbQuery try {
            UsersEntity.new {
                username = user.username
                password = user.password
                salt = user.salt
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun userAlreadyExists(username: String): Boolean = dbQuery {
        return@dbQuery UsersEntity.find { UsersTable.username eq username }.singleOrNull() != null
    }
}
package aasmc.ru.security.database.dao

import aasmc.ru.security.model.user.User

interface SecurityDAOFacade {
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun userAlreadyExists(username: String): Boolean
}
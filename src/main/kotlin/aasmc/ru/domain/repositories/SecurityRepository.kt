package aasmc.ru.domain.repositories

import aasmc.ru.domain.model.User

interface SecurityRepository {
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun userAlreadyExists(username: String): Boolean
}
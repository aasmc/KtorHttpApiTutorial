package aasmc.ru.data.cache.daos.interfaces

import aasmc.ru.data.cache.models.CachedUser


interface SecurityDAO {
    suspend fun getUserByUsername(username: String): CachedUser?
    suspend fun insertUser(user: CachedUser): Boolean
    suspend fun userAlreadyExists(username: String): Boolean
}
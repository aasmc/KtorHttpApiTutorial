package aasmc.ru.data.cache.daos.interfaces

import aasmc.ru.data.cache.models.CachedUser
import aasmc.ru.domain.model.Result


interface SecurityDAO {
    suspend fun getUserByUsername(username: String): Result<CachedUser?>
    suspend fun insertUser(user: CachedUser): Result<Unit>
    suspend fun userAlreadyExists(username: String): Result<Boolean>
}
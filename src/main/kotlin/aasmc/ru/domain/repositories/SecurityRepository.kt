package aasmc.ru.domain.repositories

import aasmc.ru.domain.model.Result
import aasmc.ru.domain.model.User

interface SecurityRepository {
    suspend fun getUserByUsername(username: String): Result<User?>
    suspend fun insertUser(user: User): Result<Unit>
    suspend fun userAlreadyExists(username: String): Result<Boolean>
}
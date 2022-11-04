package aasmc.ru.data.cache.models.mappers

import aasmc.ru.data.Mapper
import aasmc.ru.data.cache.models.CachedUser
import aasmc.ru.domain.model.User

class UsersMapper: Mapper<CachedUser, User> {
    override fun mapToDomain(entity: CachedUser): User = User(
        id = entity.id,
        username = entity.username,
        password = entity.password,
        salt = entity.salt
    )

    override fun mapToEntity(domain: User): CachedUser = CachedUser(
        id = domain.id,
        username = domain.username,
        password = domain.password,
        salt = domain.salt
    )
}
package aasmc.ru.security.model.user

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

data class User(
    val id: Long = 0,
    val username: String,
    val password: String,
    val salt: String
)


object UsersTable: LongIdTable("users") {
    val username = varchar("username", 30)
    val password = varchar("password", 100)
    val salt = varchar("salt", 100)
}

class UsersEntity(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<UsersEntity>(UsersTable)

    var username by UsersTable.username
    var password by UsersTable.password
    var salt by UsersTable.salt
}
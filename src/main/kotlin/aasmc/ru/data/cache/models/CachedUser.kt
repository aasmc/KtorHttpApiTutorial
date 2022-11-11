package aasmc.ru.data.cache.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
class CachedUser(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,
    @Column(name = "username", nullable = false)
    var username: String,
    @Column(name = "password", nullable = false)
    var password: String,
    @Column(name = "salt", nullable = false)
    var salt: String
) {

    override fun toString(): String {
        return "CachedUser: [id=$id, username=$username]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: CachedUser = (other as? CachedUser) ?: return false
        return username == o.username && password == o.password && salt == o.salt
    }

    override fun hashCode(): Int {
        return Objects.hash(username, password, salt)
    }

}
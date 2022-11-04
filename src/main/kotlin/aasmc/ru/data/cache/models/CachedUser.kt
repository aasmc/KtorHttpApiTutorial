package aasmc.ru.data.cache.models

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class CachedUser(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,
) {
    @Column(name = "username", nullable = false)
    var username: String = ""
    @Column(name = "password", nullable = false)
    var password: String = ""
    @Column(name = "salt", nullable = false)
    var salt: String = ""

    constructor(id: Long, username: String, password: String, salt: String): this(id) {
        this.username = username
        this.password = password
        this.salt = salt
    }
}
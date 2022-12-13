package aasmc.ru.playground.quering

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "USERS")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var username: String = "",
    @NotNull
    var firstname: String = "",
    @NotNull
    var lastname: String = "",
    @NotNull
    var activated: Boolean = false,
    @Embedded
    var address: Address = Address()
) {
}
package aasmc.ru.playground.fetching.nplusoneselects

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "USERS")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var username: String = ""
)
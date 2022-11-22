package aasmc.ru.playground.fetching.readonly

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "USERS")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,

    @NotNull
    var username: String = ""
)
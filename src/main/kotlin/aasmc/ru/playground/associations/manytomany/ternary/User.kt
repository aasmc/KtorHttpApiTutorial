package aasmc.ru.playground.associations.manytomany.ternary

import jakarta.persistence.*

@Entity
@Table(name = "USERS")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    var userName: String = ""
)
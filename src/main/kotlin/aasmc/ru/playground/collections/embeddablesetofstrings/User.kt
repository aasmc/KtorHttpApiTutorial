package aasmc.ru.playground.collections.embeddablesetofstrings

import jakarta.persistence.*

@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    var username: String = ""

    @Embedded
    var address: Address = Address()
}
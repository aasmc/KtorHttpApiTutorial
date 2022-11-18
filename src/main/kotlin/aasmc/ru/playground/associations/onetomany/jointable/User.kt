package aasmc.ru.playground.associations.onetomany.jointable

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
class User constructor() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    var username: String = ""

    @OneToMany(mappedBy = "buyer")
    var boughtItems: MutableSet<Item> = hashSetOf()

    constructor(name: String): this() {
        this.username = name
    }
}
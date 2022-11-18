package aasmc.ru.playground.associations.onetomany.jointable

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
class Item constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    var name: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ITEM_BUYER",
        joinColumns = [
            JoinColumn(name = "ITEM_ID")
        ],
        inverseJoinColumns = [
            JoinColumn(nullable = false)
        ]
    )
    var buyer: User? = null

    constructor(name: String) : this() {
        this.name = name
    }
}
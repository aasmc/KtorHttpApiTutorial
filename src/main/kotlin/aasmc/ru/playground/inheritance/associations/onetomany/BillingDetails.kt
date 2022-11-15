package aasmc.ru.playground.inheritance.associations.onetomany

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

// Can not be @MappedSuperclass when it's a target class in associations!
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class BillingDetails constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    var owner: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    var user: User? = null

    constructor(owner: String):this() {
        this.owner = owner
    }

    fun pay(amount: Int) {
        println("User: ${user?.username} paying amount: $amount")
    }
}
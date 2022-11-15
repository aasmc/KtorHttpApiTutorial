package aasmc.ru.playground.inheritance.associations.manytoone

import aasmc.ru.playground.inheritance.joined.BillingDetails
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "USERS")
class User constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    var username: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    var defaultBilling: BillingDetails? = null

    constructor(username: String): this() {
        this.username = username
    }
}
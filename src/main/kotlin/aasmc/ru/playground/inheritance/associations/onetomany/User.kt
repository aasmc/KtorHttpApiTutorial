package aasmc.ru.playground.inheritance.associations.onetomany

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

    @OneToMany(mappedBy = "user")
    var billingDetails: MutableSet<BillingDetails> = HashSet()

    constructor(username: String): this() {
        this.username = username
    }
}
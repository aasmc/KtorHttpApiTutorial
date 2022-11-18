package aasmc.ru.playground.associations.onetoone.foreigngenerators

import jakarta.persistence.*

@Entity
@Table(name = "USERS")
class User protected constructor() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    var username: String = ""

    @OneToOne(
        mappedBy = "user",
        cascade = [CascadeType.PERSIST]
    )
    // We can't get lazy loading of Address since it is optional
    var shippingAddress: Address? = null

    constructor(username: String) : this() {
        this.username = username
    }
}
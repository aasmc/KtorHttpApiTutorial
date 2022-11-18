package aasmc.ru.playground.associations.onetoone.foreignkey

import jakarta.persistence.*

@Entity
@Table(name = "USERS")
class User protected constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    var username: String = ""

    @OneToOne(
        // No problem with lazy loading here (unlike shared primary key)
        fetch = FetchType.LAZY, // Defaults to EAGER
        optional = false,
        cascade = [CascadeType.PERSIST]
    )
    // this mapping is a Foreign Key to Address
    @JoinColumn(unique = true) // defaults to SHIPPINGADDRESS_ID
    var shippingAddress: Address = Address()
}
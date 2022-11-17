package aasmc.ru.playground.associations.onetoone.sharedprimarykey

import jakarta.persistence.*

@Entity
@Table(name = "USERS")
class User protected constructor() {


    // we don't declare the identifier generator to allow for OneToOne mapping
    @Id
    var id: Long = 0

    var username: String = ""

    // weakly enforces the construction of User with id
    constructor(id: Long, username: String) : this() {
        this.id = id
        this.username = username
    }


    @OneToOne(
        fetch = FetchType.LAZY, // Defaults to EAGER
        optional = false // Required for lazy loading with proxies!
    )
    // this mapping means that both USER and ADDRESS must have the same
    // primary key value. The main difficulty is to ensure that associated
    // instances are assigned the same primary key when the instances are saved.
    @PrimaryKeyJoinColumn
    var shippingAddress: Address? = null
}
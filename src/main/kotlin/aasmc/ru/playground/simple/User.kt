package aasmc.ru.playground.simple

import jakarta.persistence.*
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Entity
@Table(name = "USERS")
@Serializable
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private var id: Long = 0L
) {

    constructor(
        id: Long,
        username: String,
        homeAddress: Address,
        billingAddress: Address? = null
    ) : this(id) {
        this.username = username
        this.homeAddress = homeAddress
        this.billingAddress = billingAddress
    }

    constructor(
        username: String,
        homeAddress: Address,
        billingAddress: Address? = null
    ) : this() {
        this.username = username
        this.homeAddress = homeAddress
        this.billingAddress = billingAddress
    }


    @Column(name = "username", nullable = false)
    var username: String = ""

    @Embedded
    var homeAddress: Address = Address()
        // we can return a copy of a field in a getter method
        // since Hibernate compares them by value, not by object identity
        // N.B. YOU MUST NOT copy or clone collections, because Hibernate
        // compares them by object identity.
        get() = Address(
            street = field.street,
            zipcode = field.zipcode,
            city = field.city
        )

    @Embedded
    // Not necessary...
    @AttributeOverrides(
        *[
            AttributeOverride(
                name = "street",
                column = Column(name = "BILLING_STREET")
            ),
            AttributeOverride(
                name = "zipcode",
                column = Column(name = "BILLING_ZIPCODE", length = 5)
            ),
            AttributeOverride(
                name = "city",
                column = Column(name = "BILLING_CITY")
            )
        ]
    )
    var billingAddress: Address? = null
        get() = if (field == null) null else Address(
            street = field!!.street,
            zipcode = field!!.zipcode,
            city = field!!.city
        )

    fun calcShippingCosts(fromLocation: Address): BigDecimal {
        // stub method
        return BigDecimal.TEN
    }

    fun getId() = id

}
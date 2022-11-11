package aasmc.ru.playground.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "USERS")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private var id: Long = 0L,
    @Column(name = "username", nullable = false)
    var username: String = "",
    @Embedded
    private var homeAddress: Address = Address(),
    @Embedded
    @AttributeOverrides(
        *[
            AttributeOverride(
                name = "street",
                column = Column(name = "BILLING_STREET")
            ),
            AttributeOverride(
                name = "city.zipcode",
                column = Column(name = "BILLING_ZIPCODE", length = 5)
            ),
            AttributeOverride(
                name = "city.name",
                column = Column(name = "BILLING_CITY_NAME")
            ),
            AttributeOverride(
                name = "city.country",
                column = Column(name = "BILLING_CITY_COUNTRY")
            )
        ]
    )
    private var billingAddress: Address? = null
) {


    fun getHomeAddress(): Address {
        // we can return a copy of a field in a getter method
        // since Hibernate compares them by value, not by object identity
        // N.B. YOU MUST NOT copy or clone collections, because Hibernate
        // compares them by object identity.
        return homeAddress.copy()
    }

    fun getBillingAddress(): Address? {
        return billingAddress?.copy()
    }

    fun calcShippingCosts(fromLocation: Address): BigDecimal {
        // stub method
        return BigDecimal.TEN
    }

    fun getId() = id

}
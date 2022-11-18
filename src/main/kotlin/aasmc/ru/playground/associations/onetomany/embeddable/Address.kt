package aasmc.ru.playground.associations.onetomany.embeddable

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Embeddable
class Address(
    @NotNull
    @Column(nullable = false)
    var street: String = "",

    @NotNull
    @Column(nullable = false, length = 5)
    var zipcode: String = "",

    @NotNull
    @Column(nullable = false)
    var city: String = ""
) {
    @OneToMany
    // this mapping adds a Foreign Key column to the Shipment table.
    @JoinColumn(
        name = "DELIVERY_ADDRESS_USER_ID",
        nullable = false
    )
    var deliveries: MutableSet<Shipment> = hashSetOf()
}
package aasmc.ru.playground.associations.onetomany.embeddablejointable

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
    // This mapping creates a separate table with USER_ID and SHIPMENT_ID columns
    // This association is optional.
    @JoinTable(
        name = "DELIVERIES",
        joinColumns = [JoinColumn(name = "USER_ID")],
        inverseJoinColumns = [JoinColumn(name = "SHIPMENT_ID")]
    )
    var deliveries: MutableSet<Shipment> = hashSetOf()
}
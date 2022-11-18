package aasmc.ru.playground.associations.onetoone.jointable

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
class Shipment protected constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    var createdOn: LocalDate = LocalDate.now()

    @NotNull
    var shipmentState: ShipmentState = ShipmentState.TRANSIT

    // Lazy loading has a twist: when Hibernate loads a Shipment, it queries both the
    // SHIPMENT and the ITEM_SHIPMENT join table. Hibernate has to know if there is a link
    // to an Item present before it can use a proxy. It does that in one outer join
    // SQL query, so you won't see any extra SQL statements. If there's a row in ITEM_SHIPMENT
    // Hibernate uses an Item placeholder.
    @OneToOne(fetch = FetchType.LAZY)
    // This mapping creates another table ITEM_SHIPMENT with a one to one
    // mapping between an Item and a Shipment. This means it is an optional
    // OneToOne mapping.
    @JoinTable(
        name = "ITEM_SHIPMENT", // Required
        joinColumns = [
            JoinColumn(name = "SHIPMENT_ID"), // Defaults to ID
        ],
        inverseJoinColumns = [
            JoinColumn(
                name = "ITEM_ID", // Defaults to AUCTION_ID
                nullable = false,
                unique = true
            )
        ]
    )
    var auction: Item? = null
}
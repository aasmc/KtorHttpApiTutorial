package aasmc.ru.playground.inheritance.mixed

import aasmc.ru.playground.inheritance.singletable.BillingDetails
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

/**
 * This subclass is extracted from the single table to its own
 * table.
 *
 * SINGLE_TABLE strategy forces all columns of subclasses to be
 * nullable. This mapping allows us to declare columns of the CREDITCARD
 * table as NOT NULL.
 */
@Entity
@DiscriminatorValue("CC")
@SecondaryTable(
    name = "CREDITCARD",
    // both the primary key for CREDITCARD table and it has a foreign key
    // constraint referencing the ID of the single hierarchy table.
    pkJoinColumns = [PrimaryKeyJoinColumn(name = "CREDITCARD_ID")]
)
class CreditCard: BillingDetails {

    constructor(): super()

    @NotNull // Ignored by JPA for DDL, strategy is SINGLE_TABLE!
    @Column(table = "CREDITCARD", nullable = false) // Override the primary table
    var cardNumber: String = ""

    @Column(table = "CREDITCARD", nullable = false)
    var expMonth: String = ""

    @Column(table = "CREDITCARD", nullable = false)
    var expYear: String = ""

    constructor(owner: String, cardNumber: String, expMonth: String, expYear: String): super(owner) {
        this.cardNumber = cardNumber
        this.expYear = expYear
        this.expMonth = expMonth
    }
}
package aasmc.ru.playground.inheritance.joined

import aasmc.ru.playground.inheritance.singletable.BillingDetails
import jakarta.persistence.Entity
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.validation.constraints.NotNull

@Entity
@PrimaryKeyJoinColumn(name = "CREDITCARD_ID")
class CreditCard: BillingDetails {

    constructor(): super()

    @NotNull
    var cardNumber: String = ""

    @NotNull
    var expMonth: String = ""

    @NotNull
    var expYear: String = ""

    constructor(owner: String, cardNumber: String, expMonth: String, expYear: String): super(owner) {
        this.cardNumber = cardNumber
        this.expYear = expYear
        this.expMonth = expMonth
    }
}
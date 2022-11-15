package aasmc.ru.playground.inheritance.associations.onetomany

import jakarta.persistence.Entity
import jakarta.validation.constraints.NotNull

@Entity
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
package aasmc.ru.playground.inheritance.singletable

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.validation.constraints.NotNull

@Entity
@DiscriminatorValue("CC")
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
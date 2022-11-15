package aasmc.ru.playground.inheritance.mappedsuperclass

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@AttributeOverride(
    name = "owner",
    column = Column(name = "CC_OWNER", nullable = false)
)
class CreditCard: BillingDetails {

    constructor(): super()

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    var cardNumber: String = ""

    @NotNull
    var expMonth: String = ""

    @NotNull
    var expYear: String = ""

    constructor(
        owner: String,
        cardNumber: String,
        expMonth: String,
        expYear: String
    ) : super(owner) {
        this.cardNumber = cardNumber
        this.expMonth = expMonth
        this.expYear = expYear
    }

}
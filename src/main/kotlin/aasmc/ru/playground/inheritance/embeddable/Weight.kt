package aasmc.ru.playground.inheritance.embeddable

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Embeddable
@AttributeOverrides(
    *[
        AttributeOverride(
            name = "name",
            column = Column(name = "WEIGHT_NAME")
        ),
        AttributeOverride(
            name = "symbol",
            column = Column(name = "WEIGHT_SYMBOL")
        )
    ]
)
class Weight : Measurement {
    constructor() : super()

    @NotNull
    @Column(name = "WEIGHT")
    var value: BigDecimal = BigDecimal.ZERO

    constructor(name: String, symbol: String, weight: BigDecimal) : super(name, symbol) {
        this.value = weight
    }

    override fun toString(): String {
        return String.format("%s%s", this.value, this.symbol)
    }

    companion object {
        fun kilograms(weight: BigDecimal): Weight = Weight(
            name = "kilograms",
            symbol = "kg",
            weight = weight
        )

        fun pounds(weight: BigDecimal): Weight = Weight(
            name = "pounds",
            symbol = "lbs",
            weight = weight
        )
    }
}
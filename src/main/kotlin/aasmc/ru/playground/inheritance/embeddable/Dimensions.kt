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
            column = Column(name = "DIMENSION_NAME")
        ),
        AttributeOverride(
            name = "symbol",
            column = Column(name = "DIMENSION_SYMBOL")
        )
    ]
)
class Dimensions : Measurement {
    @NotNull
    var depth: BigDecimal = BigDecimal.ZERO

    @NotNull
    var height: BigDecimal = BigDecimal.ZERO

    @NotNull
    var width: BigDecimal = BigDecimal.ZERO

    constructor() : super()

    constructor(
        name: String,
        symbol: String,
        width: BigDecimal,
        height: BigDecimal,
        depth: BigDecimal
    ) : super(
        name,
        symbol
    ) {
        this.height = height
        this.width = width
        this.depth = depth
    }

    override fun toString(): String {
        return String.format(
            "W:%s%s x H:%s%s x D:%s%s",
            this.height,
            this.symbol,
            this.width,
            this.symbol,
            this.depth,
            this.symbol
        )
    }

    companion object {
        fun centimeters(
            width: BigDecimal,
            height: BigDecimal,
            depth: BigDecimal
        ): Dimensions = Dimensions(
            name = "centimeters",
            symbol = "cm",
            width = width,
            height = height,
            depth = depth
        )

        fun inches(
            width: BigDecimal,
            height: BigDecimal,
            depth: BigDecimal
        ): Dimensions = Dimensions(
            name = "inches",
            symbol = "\"",
            width = width,
            height = height,
            depth = depth
        )
    }
}
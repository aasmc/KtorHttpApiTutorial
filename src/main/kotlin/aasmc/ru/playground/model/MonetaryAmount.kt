package aasmc.ru.playground.model

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class MonetaryAmount(
    val value: BigDecimal,
    val currency: Currency
) : Serializable {

    override fun toString(): String {
        return "$value $currency"
    }

    companion object {
        fun fromString(s: String): MonetaryAmount {
            val split = s.split(" ")
            return MonetaryAmount(
                BigDecimal(split[0]),
                Currency.getInstance(split[1])
            )
        }
    }
}
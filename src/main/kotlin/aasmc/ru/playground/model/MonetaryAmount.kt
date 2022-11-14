package aasmc.ru.playground.model

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

/**
 * When Hibernate stores entity instance data in the shared second-level cache, it
 * disassembles the entity's state. If an entity has a [MonetaryAmount] property,
 * the serialized representation of the property value is stored in the second-level
 * cache region. When entity data is retrieved from the cache region, the property
 * value is deserialized and reassembled. That is why the class must be Serializable.
 */
data class MonetaryAmount(
    val value: BigDecimal = BigDecimal.ZERO,
    val currency: Currency = Currency.getInstance(Locale.getDefault())
) : Serializable {

    override fun toString(): String {
        return "$value $currency"
    }

    companion object {
        @JvmStatic
        fun fromString(s: String): MonetaryAmount {
            val split = s.split(" ")
            return MonetaryAmount(
                BigDecimal(split[0]),
                Currency.getInstance(split[1])
            )
        }
    }
}
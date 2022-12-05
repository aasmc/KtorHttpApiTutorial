package aasmc.ru.playground.filtering.cascade

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Entity
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var amount: BigDecimal = BigDecimal.ZERO,
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    var item: Item = Item()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: Bid = (other as? Bid) ?: return false
        if (this.amount != o.amount) return false
        if (this.item.id != o.item.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + item.id.hashCode()
        return result
    }
}
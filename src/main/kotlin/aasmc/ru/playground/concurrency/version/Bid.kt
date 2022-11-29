package aasmc.ru.playground.concurrency.version

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.math.BigDecimal

@Entity
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    @NotNull
    var amount: BigDecimal = BigDecimal.ZERO,
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    var item: Item = Item()
) {

    constructor(amount: BigDecimal, item: Item, lastBid: Bid?) : this() {
        if (lastBid != null && amount.compareTo(lastBid.amount) < 1) {
            throw InvalidBidException(
                "Bid amount '$amount' to low, last bid was: ${lastBid.amount}"
            )
        }
        this.amount = amount
        this.item = item
    }

}
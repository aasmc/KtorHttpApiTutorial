package aasmc.ru.playground.simple

import jakarta.persistence.*
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.*

@Entity
data class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private var id: Long = 0
) {
    @Version
    var version: Long = 0

    @NotNull
    @Size(
        min = 2,
        max = 255,
        message = "Name is required, maximum 255 characters."
    )
    @Column(name = "name", nullable = false)
    var name: String = ""

    @Future
    var auctionEnd: Date = Date()

    @Column(name = "buy_now_price")
    var buyNowPrice: BigDecimal = BigDecimal.ZERO

    @Transient
    private var bids: MutableSet<Bid> = hashSetOf()

    fun getBids(): Set<Bid> {
        return bids.toSet()
    }

    @ManyToOne(fetch = FetchType.LAZY)
    var category: Category = Category()

    constructor(
        name: String,
        auctionEnd: Date = Date(),
        buyNowPrice: BigDecimal = BigDecimal.ZERO,
        category: Category = Category()
    ) : this() {
        this.name = name
        this.category = category
        this.buyNowPrice = buyNowPrice
        this.auctionEnd = auctionEnd
    }

    fun addBid(bid: Bid) {
        if (bid.item != null) {
            throw IllegalStateException("Bid is already assigned to an Item ${bid.item}")
        }
        bids.add(bid)
        bid.item = this
    }

    fun placeBid(currentHighestBid: Bid?, bidAmount: BigDecimal): Bid? {
        if (currentHighestBid == null ||
            bidAmount > currentHighestBid.amount
        ) {
            return Bid(bidAmount, this)
        }
        return null
    }

    fun getId() = id
}

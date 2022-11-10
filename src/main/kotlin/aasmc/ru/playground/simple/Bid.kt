package aasmc.ru.playground.simple

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Entity
data class Bid(
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "bid_sequence_generator"
    )
    @SequenceGenerator(
        name = "bid_sequence_generator",
        sequenceName = "bid_seq",
        allocationSize = 20,
    )
    @Column(name = "id", updatable = false, nullable = false)
    private var id: Long = 0
) {
    @NotNull
    @Column(name = "amount", nullable = false)
    var amount: BigDecimal = BigDecimal.ZERO

    /**
     * One Item can have many Bids.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    var item: Item? = null

    fun getId(): Long = id

    constructor(amount: BigDecimal, item: Item) : this() {
        this.item = item
        this.amount = amount
    }
}

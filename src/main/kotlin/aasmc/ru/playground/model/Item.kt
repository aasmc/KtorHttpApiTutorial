package aasmc.ru.playground.model

import jakarta.persistence.*
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.math.BigDecimal
import java.sql.Blob
import java.time.Instant
import java.time.temporal.ChronoUnit

@Entity
data class Item(
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "item_sequence_generator"
    )
    @GenericGenerator(
        name = "item_sequence_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = [
            Parameter(name = "sequence_name", value = "item_seq"),
            Parameter(name = "initial_value", value = "1"),
            Parameter(name = "increment_size", value = "3"),
            Parameter(name = "optimizer", value = "pooled-lo")
        ]
    )
    @Column(name = "id", nullable = false, updatable = false)
    private var id: Long = 0
) {
    @Version
    private var version: Long = 0

    // JPA says @Temporal is required but Hibernate will default to TIMESTAMP without it
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    // in-VM INSERT strategy, Hibernate wil use the current timestamp of the JVM
    // as the insert value for the attribute
    @org.hibernate.annotations.CreationTimestamp
    var createdOn: Instant = Instant.now()

    @NotNull
    @Basic(fetch = FetchType.LAZY, optional = false) // defaults to EAGER
    var description: String = ""

    @Basic(fetch = FetchType.LAZY)
    @Column(length = 131072, nullable = true) // 128 kilobytes maximum for the picture
    var image: ByteArray? = null

    @Lob
    var imageBlog: Blob? = null

    @NotNull
    @Enumerated(EnumType.STRING)
    var auctionType: AuctionType = AuctionType.HIGHEST_BID

    // Values of derived properties are calculated at runtime by evaluating
    // an SQL expression declared with this annotation. I.e.
    // these formulas are evaluated every time the Item entity is retrieved
    // from the DB, and not at any other time, so the result may be outdated
    // if other properties are modified. These properties never appear in
    // SQL INSERTs or UPDATEs.
    @org.hibernate.annotations.Formula(
        "substr(DESCRIPTION, 1, 12) || '...'"
    )
    var shortDescription: String = ""

    @org.hibernate.annotations.Formula(
        "(select avg(b.AMOUNT) from BID b where b.ITEM_ID = ID)"
    )
    var averageAmount: BigDecimal = BigDecimal.ZERO

    // values in the DB are stored in imperial system
    // while the domain model has metric system, so we can use a
    // Hibernate transformer:
    @Column(name = "IMPERIALWEIGHT")
    @org.hibernate.annotations.ColumnTransformer(
        read = "IMPERIALWEIGHT / 2.20462",
        write = "? * 2.20462"
    )
    var metricWeight: Double = 0.0

    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false)
    // in-DB strategy of inserting a generated value
    @org.hibernate.annotations.Generated(
        org.hibernate.annotations.GenerationTime.ALWAYS
    )
    var lastModifier: Instant = Instant.now()

    @Column(insertable = false)
    @org.hibernate.annotations.ColumnDefault("1.00")
    @org.hibernate.annotations.Generated(
        org.hibernate.annotations.GenerationTime.INSERT
    )
    var initialPrice: BigDecimal = BigDecimal.ZERO

    @NotNull
    @Size(
        min = 2,
        max = 255,
        message = "Name is required, maximum 255 characters."
    )
    @Column(name = "item_name", nullable = false)
    var name: String = ""

    @Future
    var auctionEnd: Instant = Instant.now().plus(10, ChronoUnit.DAYS)

    @Column(name = "buy_now_price", nullable = false)
    var buyNowPrice: BigDecimal = BigDecimal.ZERO

    @Transient
    private var bids: MutableSet<Bid> = hashSetOf()

    fun getBids(): Set<Bid> {
        return bids.toSet()
    }

    @ManyToOne(fetch = FetchType.LAZY)
    var category: Category? = null

    @JvmOverloads
    constructor(
        name: String,
        auctionEnd: Instant,
        buyNowPrice: BigDecimal = BigDecimal.ZERO,
        category: Category? = null
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

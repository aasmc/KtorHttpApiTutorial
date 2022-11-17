package aasmc.ru.playground.model

import aasmc.ru.playground.converters.MonetaryAmountConverter
import aasmc.ru.playground.converters.MonetaryAmountUserType
import jakarta.persistence.*
import jakarta.persistence.CascadeType
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CompositeType
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.math.BigDecimal
import java.sql.Blob
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * In this entity I use ID as the main field for equals method,
 * and always return the same value from hashCode(), because
 * I have no valid business key in the entity, that is unique and
 * immutable during the entire lifecycle of the entity.
 *
 * According to Vlad Mihalcea article:
 * https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
 * It is possible to use this scenario, since we should never fetch
 * thousands of entities in a @OneToMany Set because the performance
 * penalty on the database side is multiple orders of magnitude higher
 * than using a single hashed bucket.
 */
@Entity
class Item(
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
    private var id: Long? = null,

    @NotNull
    @Basic(fetch = FetchType.LAZY, optional = false) // defaults to EAGER
    var description: String = "",

    @NotNull
    @Enumerated(EnumType.STRING)
    var auctionType: AuctionType = AuctionType.HIGHEST_BID,

    @Column(insertable = false)
    @org.hibernate.annotations.ColumnDefault("1.00")
    @org.hibernate.annotations.Generated(
        org.hibernate.annotations.GenerationTime.INSERT
    )
    var initialPrice: BigDecimal = BigDecimal.ZERO,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "custom_price_amount"))
    @AttributeOverride(name = "currency", column = Column(name = "custom_price_currency"))
    @CompositeType(MonetaryAmountUserType::class)
    var customPrice: MonetaryAmount? = null,

    @NotNull
    @Size(
        min = 2,
        max = 255,
        message = "Name is required, maximum 255 characters."
    )
    @Column(name = "item_name", nullable = false)
    var name: String = "",

    @Convert(
        converter = MonetaryAmountConverter::class,
        disableConversion = false
    )
    @Column(name = "PRICE", nullable = false, length = 63)
    var buyNowPrice: MonetaryAmount = MonetaryAmount(),

    @ManyToOne(fetch = FetchType.LAZY)
    var category: Category? = null,
) {

    @Basic(fetch = FetchType.LAZY)
    @Column(length = 131072, nullable = true) // 128 kilobytes maximum for the picture
    var image: ByteArray? = null

    /**
     * Type [Blob] annotated with [Lob] ensures that
     * the value is loaded lazily from the DB without any
     * bytecode instrumentation.
     *
     * Blob and Clob JDBC classes include behaviour to load values on demand.
     * When the owning entity instance is loaded, the property value is a placeholder,
     * and the real value isn't immediately materialized. Once you access the property,
     * within the same transaction, the value is materialized or even streamed directly
     * (to the client) without consuming temporary memory.
     */
    @Lob
    var imageBlog: Blob? = null

    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false)
    // in-DB strategy of inserting a generated value
    @org.hibernate.annotations.Generated(
        org.hibernate.annotations.GenerationTime.ALWAYS
    )
    var lastModified: Instant = Instant.now()


    // JPA says @Temporal is required but Hibernate will default to TIMESTAMP without it
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    // in-VM INSERT strategy, Hibernate wil use the current timestamp of the JVM
    // as the insert value for the attribute
    @org.hibernate.annotations.CreationTimestamp
    var createdOn: Instant = Instant.now()

    @Version
    private var version: Long = 0

    @Future
    var auctionEnd: Instant = Instant.now().plus(10, ChronoUnit.DAYS)

    @OneToMany(
        mappedBy = "item",
        fetch = FetchType.LAZY, // the default
        // deletion of all the bids is inefficient, because Hibernate has to load
        // all bids and delete them one by one, instead of executing a single
        // query: DELETE FROM BIDS WHERE BIDS.ITEM_ID = ITEM_ID
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE],
        orphanRemoval = true
    )
    // This annotation creates a DB ForeignKey ON DELETE option, i.e.
    // if an Item is removed from the DB and it has BIDs, referencing
    // to it by ForeignKey, then the BIDs will be removed efficiently,
    // in a single DELETE statement. No BID will be loaded into the application
    // memory, deletion will happen in the DB.
    // This option is valid if Hibernate generates schema.
    // N.B. It's the programmer's responsibility to clean up any references,
    // after the deletion happened in the DB.
    // Additionally, the BID instances don't go through the regular life cycle, and
    // callbacks such as @PreRemove have no effect. And Hibernate doesn't automatically
    // clear the optional second-level cache, which potentially contains stale data.
    @org.hibernate.annotations.OnDelete(
        action = org.hibernate.annotations.OnDeleteAction.CASCADE
    )
    private var bids: MutableSet<Bid> = hashSetOf()

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

    fun getBids(): Set<Bid> {
        return bids.toSet()
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
            return Bid(amount = bidAmount, item = this)
        }
        return null
    }

    fun getId() = id

    override fun toString(): String {
        return "Item: [id=$id, " +
                "name=$name, " +
                "categoryName=${category?.name}, " +
                "createdOn=$createdOn, " +
                "lastModified=$lastModified]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: Item = (other as? Item) ?: return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

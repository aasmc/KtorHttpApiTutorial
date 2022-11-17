package aasmc.ru.playground.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

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
class Bid(
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
    private var id: Long? = null,

    @NotNull
    @Column(name = "amount", nullable = false)
    var amount: BigDecimal = BigDecimal.ZERO,

    /**
     * One Item can have many Bids. Default FetchType is EAGER
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    var item: Item? = null,
) {
    fun getId(): Long? = id

    override fun toString(): String {
        return "Bid [id=$id, amount=$amount]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: Bid = (other as? Bid) ?: return false
        return id != null && id == o.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

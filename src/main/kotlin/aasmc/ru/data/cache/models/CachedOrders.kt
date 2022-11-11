package aasmc.ru.data.cache.models

import kotlinx.serialization.Serializable
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "orders")
@Serializable
class CachedOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Long = 0,
    @Column(name = "number", nullable = false, unique = true)
    var number: String,
) {

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var items: MutableSet<CachedItem> = mutableSetOf()

    override fun toString(): String {
        return "CachedOrder: [id=$id, number=$number]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: CachedOrder = (other as? CachedOrder) ?: return false
        return Objects.equals(number, o.number)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(number)
    }
}

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
@Table(name = "items")
@Entity
@Serializable
class CachedItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Long? = null,
    @Column(name = "item", nullable = false)
    var item: String = "",
    @Column(name = "amount", nullable = false)
    var amount: Int = 0,
    @Column(name = "price", nullable = false)
    var price: Double = 0.0
) {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    var order: CachedOrder? = null

    override fun toString(): String {
        return "CachedItem: [id=$id, item=$item, amount=$amount, price=$price]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val o: CachedItem = (other as? CachedItem) ?: return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}


package aasmc.ru.data.cache.models

import kotlinx.serialization.Serializable
import jakarta.persistence.*

@Entity
@Table(name = "orders")
@Serializable
data class CachedOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Long = 0,
): java.io.Serializable {
    @Column(name = "number", nullable = false)
    var number: String = ""
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var items: MutableSet<CachedItem> = mutableSetOf()

    constructor(number: String, items: List<CachedItem> = emptyList()): this() {
        this.number = number
        this.items = items.toMutableSet()
    }
}

@Table(name = "items")
@Entity
@Serializable
data class CachedItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    var id: Long = 0,
): java.io.Serializable {
    @Column(name = "item", nullable = false)
    var item: String = ""
    @Column(name = "amount", nullable = false)
    var amount: Int = 0
    @Column(name = "price", nullable = false)
    var price: Double = 0.0
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    lateinit var order: CachedOrder

    constructor(item: String, amount: Int, price: Double): this() {
        this.item = item
        this.amount = amount
        this.price = price
    }
}


package aasmc.ru.playground.fetching.proxy

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.time.LocalDate

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var name: String = "",
    @NotNull
    var auctionEnd: LocalDate = LocalDate.now(),
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var seller: User = User()
) {
    @ManyToMany(mappedBy = "items")
    var categories: MutableSet<Category> = hashSetOf()

    @OneToMany(mappedBy = "item")
    /**
     * This annotation allows the collection to support operations
     * that don't trigger initialization: e.g. getSize(), isEmpty(), contains(),
     * containsKey(), containsValue() etc.
     */
    @org.hibernate.annotations.LazyCollection(
        org.hibernate.annotations.LazyCollectionOption.EXTRA
    )
    var bids: MutableSet<Bid> = hashSetOf()
}
package aasmc.ru.playground.fetching.batch

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
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
    @OneToMany(mappedBy = "item")
    /**
     * This setting ensures, that if one Item's bids collection gets
     * initialized, bids collections of 5 other Items are also
     * initialized with the same select.
     */
    @org.hibernate.annotations.BatchSize(size = 5)
    var bids: MutableSet<Bid> = hashSetOf()
}
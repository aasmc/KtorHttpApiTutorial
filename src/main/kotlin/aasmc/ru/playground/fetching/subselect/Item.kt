package aasmc.ru.playground.fetching.subselect

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
     * This setting tells Hibernate to initialize all bids collections for all
     * loaded into the persistence context Items as soon as you force the
     * initialization of one bids collection.
     */
    @org.hibernate.annotations.Fetch(
        org.hibernate.annotations.FetchMode.SUBSELECT
    )
    var bids: MutableSet<Bid> = hashSetOf()
}
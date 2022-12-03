package aasmc.ru.playground.fetching.cartesianproduct

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    val name: String = "",
    @NotNull
    val auctionEnd: LocalDate = LocalDate.now(),
    /**
     * To implement eager fetch plan, Hibernate uses SQL JOIN operation to load
     * an Item and a User instance in one SELECT.
     * Eager fetching with the default JOIN strategy isn’t problematic for
     * @ManyToOne and @OneToOne associations.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    var seller: User = User(),
) {
    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
    var bids: MutableSet<Bid> = hashSetOf()

    /**
     * Eagerly loading collections with JOINs, on the other hand, can lead to
     * serious performance issues. If you also switched to FetchType.EAGER
     * for the bids and images collections, you’d run into the Cartesian product problem.
     *
     * It doesn’t matter whether both collections are @OneToMany, @ManyToMany,
     * or @ElementCollection. Eager fetching more than one collection at once with
     * the SQL JOIN operator is the fundamental issue, no matter what the collection
     * content is.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "IMAGE")
    @Column(name = "FILENAME")
    var images: MutableSet<String> = hashSetOf()

    // ...
}
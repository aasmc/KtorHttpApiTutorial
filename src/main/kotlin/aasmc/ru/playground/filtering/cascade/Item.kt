package aasmc.ru.playground.filtering.cascade

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var name: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_ID", nullable = false)
    /**
     * Replicate's mane use case is copying data from one database into
     * another. When you call replicate() on a detached Item, Hibernate executes
     * SQL SELECT statements to find out whether the Item and its seller are already
     * present in the DB. Then, on commit, when the persistence context is flushed,
     * Hibernate writes the values of the Item and the seller into the target DB.
     */
    @org.hibernate.annotations.Cascade(
        org.hibernate.annotations.CascadeType.REPLICATE
    )
    var seller: User = User()
) {
    @OneToMany(
        mappedBy = "item",
        cascade = [CascadeType.DETACH, CascadeType.MERGE]
    )
    var bids: MutableSet<Bid> = hashSetOf()

}
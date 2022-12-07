package aasmc.ru.playground.filtering.envers

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
/**
 * This annotation tells Hibernate Envers to create additional tables
 * which will store revision information for the Audited entity.
 *
 * When you modify data and commit a transaction, Hibernate inserts a new
 * revision number with a timestamp into the REVINFO table. Then for each
 * modified and audited entity instance involved in the change set, a copy
 * of its data is stored in the audit tables. Foreign keys on revision number
 * columns link the change set together.
 */
@org.hibernate.envers.Audited
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,

    @NotNull
    var name: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_ID", nullable = false)
    var seller: User = User(),
) {

    @OneToMany(mappedBy = "item")
    @org.hibernate.envers.NotAudited
    var bids: MutableSet<Bid> = hashSetOf()
}

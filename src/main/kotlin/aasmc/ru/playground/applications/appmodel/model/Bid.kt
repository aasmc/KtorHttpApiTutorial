package aasmc.ru.playground.applications.appmodel.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.*

@Entity
@org.hibernate.annotations.Immutable
class Bid(
    @Id
    /**
     * In Hibernate 5.0 and above this mapping will select a [SequenceStyleGenerator].
     */
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name = "sequence", allocationSize = 3)
    var id: Long? = null,
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    var item: Item? = null,
    @NotNull
    @Column(updatable = false)
    var amount: BigDecimal = BigDecimal.ZERO,
    @NotNull
    @Column(updatable = false)
    var createdOn: Date = Date()
) : Comparable<Bid> {

    override fun compareTo(other: Bid): Int {
        return other.amount.compareTo(amount)
    }
}
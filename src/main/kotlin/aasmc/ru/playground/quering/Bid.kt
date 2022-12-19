package aasmc.ru.playground.quering

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Entity
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "FK_BID_ITEM_ID"))
    var item: Item = Item(),

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "FK_BID_BIDDER_ID"))
    var bidder: User? = User(),

    @NotNull
    var amount: BigDecimal = BigDecimal.ZERO
)
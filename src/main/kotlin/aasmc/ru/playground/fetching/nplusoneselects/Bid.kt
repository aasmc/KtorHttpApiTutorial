package aasmc.ru.playground.fetching.nplusoneselects

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
    var item: Item = Item(),
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var bidder: User = User(),
    @NotNull
    var amount: BigDecimal = BigDecimal.ZERO
) {
}
package aasmc.ru.playground.fetching.proxy

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.math.BigDecimal

@Entity
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var amount: BigDecimal = BigDecimal.ZERO,
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var item: Item = Item(),
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var bidder: User = User()
) {
}
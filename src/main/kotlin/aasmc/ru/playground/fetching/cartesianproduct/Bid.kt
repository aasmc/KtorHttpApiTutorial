package aasmc.ru.playground.fetching.cartesianproduct

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
    var amount: BigDecimal = BigDecimal.ZERO
)
package aasmc.ru.playground.filtering.envers

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Entity
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var amount: BigDecimal = BigDecimal.ZERO,
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    var item: Item? = null
) {
}
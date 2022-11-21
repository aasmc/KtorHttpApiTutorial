package aasmc.ru.playground.associations.maps.mapkey

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Entity
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    var amount: BigDecimal = BigDecimal.ZERO,
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    var item: Item = Item()
) {
}
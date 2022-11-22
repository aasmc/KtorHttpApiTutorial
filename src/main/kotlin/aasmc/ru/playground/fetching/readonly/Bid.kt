package aasmc.ru.playground.fetching.readonly

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Entity
@org.hibernate.annotations.Immutable
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var item: Item = Item(),

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var bidder: User = User(),

    var amount: BigDecimal = BigDecimal.ZERO
)
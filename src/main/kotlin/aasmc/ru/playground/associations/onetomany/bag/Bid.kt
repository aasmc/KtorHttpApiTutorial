package aasmc.ru.playground.associations.onetomany.bag

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Entity
class Bid constructor() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var item: Item = Item()

    var amount: BigDecimal = BigDecimal.ZERO

    constructor(amount: BigDecimal, item: Item) : this() {
        this.item = item
        this.amount = amount
    }
}
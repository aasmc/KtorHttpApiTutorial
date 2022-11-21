package aasmc.ru.playground.associations.complexschemas.custom

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

@Entity
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(
        name = "ITEM_ID",
        nullable = false,
        foreignKey = ForeignKey(name = "FK_ITME_ID")
    )
    var item: Item = Item(),

    @NotNull
    var amount: BigDecimal = BigDecimal.ZERO,

    @NotNull
    var createdOn: LocalDate = LocalDate.now()
) {
}
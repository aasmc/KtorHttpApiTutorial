package aasmc.ru.playground.associations.complexschemas.custom

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
@org.hibernate.annotations.Check(
    constraints = "AUCTIONSTART < AUCTIONEND"
)
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,

    var name: String = "",

    @NotNull
    var auctionEnd: LocalDate = LocalDate.now().plusDays(10),

    @NotNull
    var auctionStart: LocalDate = LocalDate.now()
) {

    @OneToMany(mappedBy = "item")
    val bids: MutableSet<Bid> = hashSetOf()
}
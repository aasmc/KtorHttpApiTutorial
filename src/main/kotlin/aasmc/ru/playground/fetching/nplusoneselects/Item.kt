package aasmc.ru.playground.fetching.nplusoneselects

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var name: String = "",
    @NotNull
    var auctionEnd: LocalDate = LocalDate.now(),
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var seller: User = User()
) {
    @OneToMany(mappedBy = "item")
    var bids: MutableSet<Bid> = hashSetOf()
}
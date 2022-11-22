package aasmc.ru.playground.fetching.readonly

import jakarta.persistence.*
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.time.temporal.ChronoUnit

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    @NotNull
    var name: String = "",
    @NotNull
    @Future
    var auctionEnd: Instant = Instant.now().plus(1, ChronoUnit.DAYS),

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var seller: User = User()
) {

    @OneToMany(mappedBy = "item")
    var bids: MutableSet<Bid> = hashSetOf()
}
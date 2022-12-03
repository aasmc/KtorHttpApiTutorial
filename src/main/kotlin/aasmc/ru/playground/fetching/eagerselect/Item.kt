package aasmc.ru.playground.fetching.eagerselect

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
    @ManyToOne(fetch = FetchType.EAGER)
    @org.hibernate.annotations.Fetch(
        org.hibernate.annotations.FetchMode.SELECT // Defaults to JOIN
    )
    var seller: User = User()
) {
    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
    /**
     * This setting tells Hibernate to perform SELECTs instead of JOINs
     * when eagerly querying for Bids. Thus we get rid of the cartesian
     * product problem.
     */
    @org.hibernate.annotations.Fetch(
        org.hibernate.annotations.FetchMode.SELECT
    )
    var bids: MutableSet<Bid> = hashSetOf()
}
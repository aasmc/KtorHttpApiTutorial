package aasmc.ru.playground.associations.onetomany.list

import jakarta.persistence.*

@Entity
class Item constructor() {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    var name: String = ""

    @OneToMany
    @JoinColumn(
        name = "ITEM_ID",
        nullable = false
    )
    @OrderColumn(
        name = "BID_POSITION", // Defaults to BIDS_ORDER
        nullable = false
    )
    var bids: MutableList<Bid> = ArrayList()

    constructor(name: String) : this() {
        this.name = name
    }
}
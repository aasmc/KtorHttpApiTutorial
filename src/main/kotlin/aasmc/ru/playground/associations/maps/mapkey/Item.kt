package aasmc.ru.playground.associations.maps.mapkey

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    @NotNull
    var name: String = ""
) {
    // this annotation maps a property of the target entity (here Bid)
    // as the key of the map. The default (if name is omitted) is the
    // identifier property of the entity.
    @MapKey(name = "id")
    @OneToMany(mappedBy = "item")
    val bids: MutableMap<Long, Bid> = hashMapOf()
}
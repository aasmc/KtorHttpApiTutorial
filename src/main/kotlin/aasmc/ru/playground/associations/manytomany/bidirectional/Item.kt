package aasmc.ru.playground.associations.manytomany.bidirectional

import jakarta.persistence.*

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    var name: String = ""
) {

    @ManyToMany(mappedBy = "items")
    // this collection is effectively read-only
    var categories: MutableSet<Category> = hashSetOf()
}
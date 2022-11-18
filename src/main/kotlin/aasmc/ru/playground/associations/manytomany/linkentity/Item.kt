package aasmc.ru.playground.associations.manytomany.linkentity

import jakarta.persistence.*

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    var name: String = ""
) {
    @OneToMany(mappedBy = "item")
    var categorizedItem: MutableSet<CategorizedItem> = hashSetOf()
}
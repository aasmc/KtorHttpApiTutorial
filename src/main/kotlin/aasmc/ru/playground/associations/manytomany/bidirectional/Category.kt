package aasmc.ru.playground.associations.manytomany.bidirectional

import jakarta.persistence.*

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    var name: String
) {
    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "CATEGORY_ITEM",
        joinColumns = [JoinColumn(name = "CATEGORY_IG")],
        inverseJoinColumns = [JoinColumn(name = "ITEM_ID")]
    )
    var items: MutableSet<Item> = hashSetOf()
}
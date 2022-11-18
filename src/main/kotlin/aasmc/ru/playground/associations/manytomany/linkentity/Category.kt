package aasmc.ru.playground.associations.manytomany.linkentity

import jakarta.persistence.*

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long,
    var name: String
) {

    @OneToMany(mappedBy = "category")
    var categorizedItems: MutableSet<CategorizedItem> = hashSetOf()
}
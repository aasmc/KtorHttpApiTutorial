package aasmc.ru.playground.quering

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var name: String = "",
    @ManyToOne
    @JoinColumn(
        name = "PARENT_ID",
        foreignKey = ForeignKey(name = "FK_CATEGORY_PARENT_ID")
    )
    // the root of the tree has no parent, column has to be nullable
    var parent: Category? = null
) {
    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "CATEGORY_ITEM",
        joinColumns = [JoinColumn(
            name = "CATEGORY_ID",
            foreignKey = ForeignKey(name = "FK_CATEGORY_ITEM_CATEGORY_ID")
        )],
        inverseJoinColumns = [JoinColumn(
            name = "ITEM_ID",
            foreignKey = ForeignKey(
                name = "FK_CATEGORY_ITEM_ITEM_ID"
            )
        )]
    )
    var items: MutableSet<Item> = hashSetOf()
}
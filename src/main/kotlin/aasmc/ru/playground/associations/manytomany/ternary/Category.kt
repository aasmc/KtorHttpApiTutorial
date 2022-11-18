package aasmc.ru.playground.associations.manytomany.ternary

import jakarta.persistence.*

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    var name: String
) {

    /**
     * This mapping will create a table: CATEGORY_ITEM
     * with 4 columns:
     * CATEGORY_ID <PK> <FK>  -  defined here
     * ITEM_ID <PK> <FK>      -  defined in CategorizedItem
     * USER_ID <FK>           -  defined in CategorizedItem
     * ADDEDON                -  defined in CategorizedItem
     */
    @ElementCollection
    @CollectionTable(
        name = "CATEGORY_ITEM",
        joinColumns = [JoinColumn(name = "CATEGORY_ID")]
    )
    var items: MutableSet<CategorizedItem> = hashSetOf()
}
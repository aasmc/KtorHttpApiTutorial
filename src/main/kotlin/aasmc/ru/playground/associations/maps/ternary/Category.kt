package aasmc.ru.playground.associations.maps.ternary

import jakarta.persistence.*

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    var name: String = ""
) {
    /**
     * This mapping creates a table CATEGORY_ITEM with the following columns:
     *  CATEGORY_ID <PK> <FK>
     *  ITEM_ID <PK> <FK>
     *  USER_ID <FK>
     *
     * The key of each map entry is an Item, and the related value is the User,
     * who added the item to the Category.
     */
    @ManyToMany(cascade = [CascadeType.PERSIST])
    @MapKeyJoinColumn(name = "ITEM_ID") // Defaults to ITEMADDEDBY_KEY
    @JoinTable(
        name = "CATEGORY_ITEM",
        joinColumns = [JoinColumn(name = "CATEGORY_ID")],
        inverseJoinColumns = [JoinColumn(name = "USER_ID")]
    )
    var itemAddedBy: MutableMap<Item, User> = hashMapOf()
}
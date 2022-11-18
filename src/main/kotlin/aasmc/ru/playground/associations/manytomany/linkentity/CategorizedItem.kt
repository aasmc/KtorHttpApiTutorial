package aasmc.ru.playground.associations.manytomany.linkentity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.time.LocalDate

@Entity
@Table(name = "CATEGORY_ITEM")

// MUST never update properties after creation!
@org.hibernate.annotations.Immutable
class CategorizedItem constructor() {

    @EmbeddedId
    var id: Id = Id()

    @Column(updatable = false)
    @NotNull
    var addedBy: String = ""

    @Column(updatable = false)
    @NotNull
    var addedOn: LocalDate = LocalDate.now()

    @ManyToOne
    @JoinColumn(
        name = "CATEGORY_ID",
        insertable = false, updatable = false
    )
    var category: Category? = null

    @ManyToOne
    @JoinColumn(
        name = "ITEM_ID",
        insertable = false, updatable = false
    )
    var item: Item? = null

    /**
     * Category and Item are required to be persistent at the time of
     * instantiation of CategorizedItem.
     */
    constructor(addedByUsername: String, category: Category, item: Item): this() {
        this.addedBy = addedByUsername
        this.category = category
        this.item = item

        // MUST assign composite key values, Hibernate doesn't generate them!
        this.id.categoryId = category.id
        this.id.itemId = item.id

        // Guarantee referential integrity if made bidirectional
        category.categorizedItems.add(this)
        item.categorizedItem.add(this)
    }

    @Embeddable
    class Id(
        var categoryId: Long = 0,
        var itemId: Long = 0
    ) : Serializable {
        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            val o: Id = (other as? Id) ?: return false
            return categoryId == o.categoryId && itemId == o.itemId
        }

        override fun hashCode(): Int {
            return categoryId.hashCode() + itemId.hashCode()
        }
    }
}
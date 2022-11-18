package aasmc.ru.playground.associations.manytomany.ternary

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.time.LocalTime

@Embeddable
class CategorizedItem constructor() {
    @ManyToOne
    @JoinColumn(
        name = "ITEM_ID",
        nullable = false, updatable = false
    )
    var item: Item = Item()

    @ManyToOne
    @JoinColumn(
        name = "USER_ID",
        updatable = false
    )
    @NotNull // Doesn't generate SQL constraint, so not part of the PK!
    var addedBy: User = User()

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @NotNull // Doesn't generate SQL constraint, so not part of the PK!
    var addedOn: LocalDateTime = LocalDateTime.now()

    constructor(addedBy: User, item: Item) : this() {
        this.addedBy = addedBy
        this.item = item
    }

    // Careful! Equality as shown here is not 'detached' safe!
    // Don't put detached instances into a HashSet! Or, if you
    // really have to compare detached instances, make sure they
    // were all loaded in the same persistence context.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val o: CategorizedItem = (other as? CategorizedItem) ?: return false

        // We are comparing instances by Java identity, not by primary key
        // equality. The scope where Java identity is the same as primary
        // key equality is the persistence context, not when instances are
        // in detached state!
        if (addedBy != o.addedBy) return false
        if (addedOn != o.addedOn) return false
        if (item != o.item) return false

        return true
    }

    override fun hashCode(): Int {
        var result = item.hashCode()
        result = 31 * result + addedBy.hashCode()
        result = 31 * result + addedOn.hashCode()
        return result
    }
}

























package aasmc.ru.playground.fetching.proxy

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import org.jetbrains.annotations.NotNull

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var name: String = ""
) {
    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "CATEGORY_ITEM",
        joinColumns = [JoinColumn(name = "CATEGORY_ID")],
        inverseJoinColumns = [JoinColumn(name = "ITEM_ID")]
    )
    var items: MutableSet<Item> = hashSetOf()
}
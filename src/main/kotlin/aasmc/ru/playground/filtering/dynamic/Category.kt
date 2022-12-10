package aasmc.ru.playground.filtering.dynamic

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var name: String = "",
) {
    @OneToMany(mappedBy = "category")
    @org.hibernate.annotations.Filter(
        name = "limitByUserRank",
        condition = ":currentUserRank >= (" +
                "select u.RANK from USERS u " +
                "where u.ID = SELLER_ID" +
                ")"
    )
    var items: MutableSet<Item> = hashSetOf()
}
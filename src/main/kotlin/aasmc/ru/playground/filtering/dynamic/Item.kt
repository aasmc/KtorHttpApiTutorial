package aasmc.ru.playground.filtering.dynamic

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@org.hibernate.annotations.FilterDefs(
    *[
        org.hibernate.annotations.FilterDef(
            name = "limitByUserRank",
            parameters = [
                org.hibernate.annotations.ParamDef(
                    name = "currentUserRank", type = Int::class
                )
            ]
        ),
        org.hibernate.annotations.FilterDef(
            name = "limitByUserRankDefault",
            defaultCondition = ":currentUserRank >= (" +
                    "select u.RANK from USERS u " +
                    "where u.ID = SELLER_ID" +
                    ")",
            parameters = [
                org.hibernate.annotations.ParamDef(
                    name = "currentUserRank", type = Int::class
                )
            ]
        )
    ]
)
@org.hibernate.annotations.Filter(
    name = "limitByUserRank",
    condition = ":currentUserRank >= (" +
            "select u.RANK from USERS u " +
            "where u.ID = SELLER_ID" +
            ")"
)
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var name: String = "",
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var category: Category = Category(),
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    var seller: User = User()
) {
}
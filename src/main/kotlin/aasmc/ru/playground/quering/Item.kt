package aasmc.ru.playground.quering

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

@NamedQueries(
    NamedQuery(
        name = "findItemById",
        query = "select i from Item i where i.id = :id"
    ),
    NamedQuery(
        name = "findItems",
        query = "select i from Item i"
    ),
    NamedQuery(
        name = "findItemByName",
        query = "select i from Item i where i.name like :name",
        hints = [
            QueryHint(
                name = org.hibernate.jpa.AvailableHints.HINT_SPEC_QUERY_TIMEOUT,
                value = "60000"
            ),
            QueryHint(
                name = org.hibernate.jpa.AvailableHints.HINT_COMMENT,
                value = "Custom SQL comment"
            )
        ]
    )
)
@SqlResultSetMappings(
    SqlResultSetMapping(
        name = "ItemResult",
        entities = [
            EntityResult(
                entityClass = Item::class,
                fields = [
                    FieldResult(name = "id", column = "ID"),
                    FieldResult(name = "name", column = "EXTENDED_NAME"),
                    FieldResult(name = "createdOn", column = "CREATEDON"),
                    FieldResult(name = "auctionEnd", column = "AUCTIONEND"),
                    FieldResult(name = "auctionType", column = "AUCTIONTYPE"),
                    FieldResult(name = "approved", column = "APPROVED"),
                    FieldResult(name = "buyNowPrice", column = "BUYNOWPRICE"),
                    FieldResult(name = "seller", column = "SELLER_ID")
                ]
            )
        ]
    )
)
@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var name: String = "",
    @NotNull
    var createdOn: LocalDate = LocalDate.now(),
    @NotNull
    var auctionEnd: LocalDate = LocalDate.now().plusDays(1),
    @NotNull
    @Enumerated(EnumType.STRING)
    var auctionType: AuctionType = AuctionType.HIGHEST_BID,
    @NotNull
    var approved: Boolean = false,

    var buyNowPrice: BigDecimal = BigDecimal.ZERO,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(name = "FK_ITEM_SELLER_ID"))
    var seller: User = User()
) {
    @ManyToMany(mappedBy = "items")
    var categories: MutableSet<Category> = hashSetOf()

    @OneToMany(mappedBy = "item")
    var bids: MutableSet<Bid> = hashSetOf()

    @ElementCollection
    @JoinColumn(foreignKey = ForeignKey(name = "FK_ITEM_IMAGES_ITEM_ID"))
    var images: MutableSet<Image> = hashSetOf()
}
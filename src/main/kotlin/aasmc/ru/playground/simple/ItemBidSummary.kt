package aasmc.ru.playground.simple

import jakarta.persistence.Entity
import jakarta.persistence.Id

/**
 * An application-leve view into the database.
 * This is a read-only entity class mapped to an SQL SELECT.
 */
@Entity
@org.hibernate.annotations.Immutable
@org.hibernate.annotations.Subselect(
    value = """
        select i.ID as ITEMID, i.ITEM_NAME as NAME,
        count(b.ID) as NUMBEROFBIDS 
        from ITEM i left outer join BID b on i.ID = b.ITEM_ID
        group by i.ID, i.ITEM_NAME
    """
)
@org.hibernate.annotations.Synchronize(*["Item", "Bid"])
class ItemBidSummary {
    @Id
    protected var itemId: Long = 0

    protected var name: String = ""

    protected var numberOfBids: Long = 0

    fun getId() = itemId

    fun getItemName() = name

    fun getBidsNumber() = numberOfBids

}
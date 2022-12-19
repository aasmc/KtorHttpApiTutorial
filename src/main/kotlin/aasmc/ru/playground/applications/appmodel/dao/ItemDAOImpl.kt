package aasmc.ru.playground.applications.appmodel.dao

import aasmc.ru.playground.applications.appmodel.model.Bid
import aasmc.ru.playground.applications.appmodel.model.Item
import aasmc.ru.playground.applications.appmodel.model.ItemBidSummary
import jakarta.persistence.criteria.JoinType
import java.math.BigDecimal
import java.util.*
import javax.ejb.Stateless

@Stateless
class ItemDAOImpl : GenericDaoImpl<Item, Long>(Item::class.java), ItemDao {

    override fun findAll(withBids: Boolean): List<Item> {
        val cb = em.criteriaBuilder
        val criteria = cb.createQuery(Item::class.java)
        val i = criteria.from(Item::class.java)
        criteria.select(i)
            .distinct(true) // In-memory distinct
            .orderBy(cb.asc(i.get<Date>("auctionEnd")))
        if (withBids) {
            i.fetch<Item, Bid>("bids", JoinType.LEFT)
        }
        return em.createQuery(criteria).resultList
    }

    override fun findByName(name: String, fuzzy: Boolean): List<Item> {
        val queryStr = if (fuzzy) {
            "select i from Item i where lower(i.name) like lower(:itemName)"
        } else {
            "select i from Item i where i.name = :itemName"
        }
        val parameter = if (fuzzy) {
            "%%$name%%"
        } else {
            name
        }
        return em.createQuery(queryStr, Item::class.java)
            .setParameter("itemName", parameter)
            .resultList
    }

    override fun findItemBidSummaries(): List<ItemBidSummary> {
        val cb = em.criteriaBuilder
        val criteria = cb.createQuery(ItemBidSummary::class.java)
        val i = criteria.from(Item::class.java)
        val b = i.join<Item, Bid>("bids", JoinType.LEFT)
        criteria.select(
            cb.construct(
                ItemBidSummary::class.java,
                i.get<Long>("id"), i.get<String>("name"), i.get<Date>("auctionEnd"),
                cb.max(b.get<BigDecimal>("amount"))
            )
        )
        criteria.orderBy(cb.asc(i.get<Date>("auctionEnd")))
        criteria.groupBy(i.get<Long>("id"), i.get<String>("name"), i.get<Date>("auctionEnd"))
        return em.createQuery(criteria).resultList
    }

}

























package aasmc.ru.playground.model

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ItemBidSummaryTest: AbstractTest() {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadSubSelectEntity() = runTest {
        val ITEM_ID = storeItemsAndBids()

        entityManagerFactory.withEntityManager {
            val itemBidSummary = find(ItemBidSummary::class.java, ITEM_ID)
            assertEquals(itemBidSummary.getItemName(), "Some item")
            clear()

            val item = find(Item::class.java, ITEM_ID)
            item.name = "New name"

//            No flush before retrieval by identifier!
//            val itemSummary = find(ItemBidSummary::class.java, ITEM_ID)

            // Automatic flush before queries if synchronized tables are affected
            val query = createQuery(
                "select ibs from ItemBidSummary ibs where ibs.itemId = :id",
                ItemBidSummary::class.java
            ).setParameter("id", ITEM_ID)
            val result = query.singleResult
            assertEquals(result.getItemName(), "New name")
        }
    }

    fun storeItemsAndBids(): Long = runBlocking {
        val result = entityManagerFactory.withEntityManager {
            val item = Item()
            item.name = "Some item"
            item.description = "This is some description"
            persist(item)
            for (i in 1..3) {
                val bid = Bid()
                bid.amount = BigDecimal(10 + i)
                bid.item = item
                persist(bid)
            }
            item.getId()!!
        }
        return@runBlocking when(result) {
            is Result.Success -> result.data
            else -> throw Exception()
        }
    }

}
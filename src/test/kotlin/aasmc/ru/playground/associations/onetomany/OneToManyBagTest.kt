package aasmc.ru.playground.associations.onetomany

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.associations.onetomany.bag.Bid
import aasmc.ru.playground.associations.onetomany.bag.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OneToManyBagTest : AbstractTest(
    entityProvider = {
        arrayOf(
            Item::class.java,
            Bid::class.java
        )
    }
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun storeLoadItemBid() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item("Some item")
            persist(item)

            val bid = Bid(amount = BigDecimal.valueOf(123.00), item = item)
            item.bids.add(bid)
            item.bids.add(bid) // No persistent effect!
            persist(bid)

            assertEquals(item.bids.size, 2)
            item.id
        }
        assertTrue(idRes is Result.Success)
        val itemId = idRes.data
        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, itemId)
            assertEquals(item.bids.size, 1)

            val bid = Bid(BigDecimal("456.00"), item)

            // No more queries are necessary as long as we don't iterate over the collection
            item.bids.add(bid) // This doesn't trigger a SELECT although we access the collection
            persist(bid)
        }
    }
}

























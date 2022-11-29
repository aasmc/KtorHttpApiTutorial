package aasmc.ru.playground.fetching

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.fetching.readonly.Bid
import aasmc.ru.playground.fetching.readonly.Item
import aasmc.ru.playground.fetching.readonly.User
import aasmc.ru.util.FetchTestData
import aasmc.ru.util.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hibernate.Session
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ReadOnly : AbstractTest(
    entityProvider = {
        arrayOf(
            Item::class.java,
            User::class.java,
            Bid::class.java
        )
    }
) {

    private fun storeTestData(): FetchTestData = runBlocking {

        val res = entityManagerFactory.withEntityManager {
            val itemIds = LongArray(3)
            val userIds = LongArray(3)
            val johndoe = User(username = "JohnDoe")
            persist(johndoe)
            userIds[0] = johndoe.id

            val janeroe = User(username = "janeroe")
            persist(janeroe)
            userIds[1] = janeroe.id

            val rob = User(username = "robertdoe")
            persist(rob)
            userIds[2] = rob.id

            val item = Item(
                name = "Item One",
                auctionEnd = Instant.now().plus(1, ChronoUnit.DAYS),
                seller = johndoe
            )
            persist(item)
            itemIds[0] = item.id
            for (i in 1..3) {
                val bid = Bid(
                    item = item, bidder = rob, amount = BigDecimal(9 + i)
                )
                item.bids.add(bid)
                persist(bid)
            }

            val item2 = Item(
                name = "Item Two",
                auctionEnd = Instant.now().plus(1, ChronoUnit.DAYS),
                seller = johndoe
            )
            persist(item2)
            itemIds[1] = item2.id

            for (i in 1..3) {
                val bid = Bid(
                    item = item2, bidder = janeroe, amount = BigDecimal(2 + i)
                )
                item2.bids.add(bid)
                persist(bid)
            }

            val item3 = Item(
                name = "Item Three",
                auctionEnd = Instant.now().plus(1, ChronoUnit.DAYS),
                seller = janeroe
            )
            persist(item3)
            itemIds[2] = item3.id

            val testData = FetchTestData(
                items = TestData(identifiers = itemIds),
                users = TestData(identifiers = userIds)
            )
            testData
        }

        assertTrue(res is Result.Success)
        res.data

    }

    @Test
    fun immutableEntity() = runTest {
        val testData = storeTestData()
        val itemId = testData.items.getFirstId()

        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, itemId)
            for (bid in item.bids) {
                bid.amount = BigDecimal("99.00") // this has no effect
            }
        }
        val itemRes = entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, itemId)
            // load bids
            for (bid in item.bids) {
                println(bid.id)
            }
            item
        }

        assertTrue(itemRes is Result.Success)
        val item = itemRes.data
        for (bid in item.bids) {
            assertNotEquals("99.00", bid.amount.toString())
        }
    }

    @Test
    fun selectiveReadOnly() = runTest {
        val testData = storeTestData()

        val itemId = testData.items.getFirstId()
        val itemRes = entityManagerFactory.withEntityManager {
            unwrap(Session::class.java).isDefaultReadOnly = true
            val item = find(Item::class.java, itemId)
            item.name = "New Name"

            flush() // NO Update

            clear()
            val itemAnother = find(Item::class.java, itemId)
            assertNotEquals("New Name", itemAnother.name)

            unwrap(Session::class.java).setReadOnly(itemAnother, true)
            itemAnother.name = "New Name"
            flush() // NO Update

            clear()
            val itemThree = find(Item::class.java, itemId)
            assertNotEquals("New Name", itemThree.name)

            val query = unwrap(Session::class.java)
                .createQuery("select i from Item i", Item::class.java)

            query.setReadOnly(true).list()
            val result = query.list()
            for (i in result) {
                i.name = "New Name"
            }
            flush() // No Update

            clear()
            find(Item::class.java, itemId)
        }

        assertTrue(itemRes is Result.Success)
        val item = itemRes.data
        assertNotEquals("New Name", item.name)
    }

    @Test
    fun multiplyQuery() = runTest {
        val testData = storeTestData()

        val res = entityManagerFactory.withEntityManager {
            val session = unwrap(Session::class.java)
            val users = session.byMultipleIds(User::class.java)
                .multiLoad(1L, 2L, 3L)

            assertEquals(3, users.size)

            val sameUsers = session
                .byMultipleIds(User::class.java)
                .enableSessionCheck(true)
                .multiLoad(1L, 2L, 3L)
            assertEquals(users, sameUsers)
        }
        assertTrue(res is Result.Success)
    }
}



























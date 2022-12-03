package aasmc.ru.playground.fetching

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.fetching.nplusoneselects.Bid
import aasmc.ru.playground.fetching.nplusoneselects.Item
import aasmc.ru.playground.fetching.nplusoneselects.User
import aasmc.ru.playground.fetching.proxy.Category
import aasmc.ru.util.TestData
import jakarta.persistence.criteria.JoinType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EagerQuery: AbstractTest(
    entityProvider = {
        arrayOf(
            Bid::class.java,
            User::class.java,
            Item::class.java
        )
    }
) {
    private fun storeTestData(): FetchTestData = runBlocking {
        val res = entityManagerFactory.withEntityManager {
            val itemIds = LongArray(3)
            val userIds = LongArray(3)
            val johnDoe = User(username = "johndoe")
            persist(johnDoe)
            userIds[0] = johnDoe.id!!

            val janeRoe = User(username = "janeroe")
            persist(janeRoe)
            userIds[1] = janeRoe.id!!

            val robDoe = User(username = "robDoe")
            persist(robDoe)
            userIds[2] = robDoe.id!!

            val item = Item(
                name = "Item One",
                auctionEnd = LocalDate.now().plusDays(1),
                seller = johnDoe
            )
            persist(item)
            itemIds[0] = item.id!!
            for (i in 1..3) {
                val bid = Bid(item = item, bidder = robDoe, amount = BigDecimal(9 + i))
                item.bids.add(bid)
                persist(bid)
            }

            val itemTwo = Item(
                name = "Item Two",
                auctionEnd = LocalDate.now().plusDays(1),
                seller = robDoe
            )
            persist(itemTwo)
            itemIds[1] = itemTwo.id!!
            for (i in 1..1) {
                val bid = Bid(item = itemTwo, amount = BigDecimal(2 + i), bidder = janeRoe)
                itemTwo.bids.add(bid)
                persist(bid)
            }

            val itemThree = Item(
                name = "Item Three",
                auctionEnd = LocalDate.now().plusDays(2),
                seller = janeRoe
            )
            persist(itemThree)
            itemIds[2] = itemThree.id!!
            for (i in 1..1) {
                val bid = Bid(item = itemThree, amount = BigDecimal(3 + i), bidder = johnDoe)
                itemThree.bids.add(bid)
                persist(bid)
            }

            val testData = FetchTestData(
                items = TestData(itemIds),
                users = TestData(userIds),
            )
            testData
        }
        assertTrue(res is Result.Success)
        res.data
    }

    @Test
    fun fetchUsers() = runTest {
        storeTestData()

        val res = entityManagerFactory.withEntityManager {
            val items = createQuery(
                "select i from Item i join fetch i.seller", Item::class.java
            ).resultList
            // select i.*, u.*
            //  from ITEM i
            //   inner join USERS u on u.ID = i.SELLER_ID
            //  where i.ID = ?
            close() // detach all
            for (i in items) {
                assertTrue(i.seller.username.isNotBlank())
            }
        }

        assertTrue(res is Result.Success)

        val res2 = entityManagerFactory.withEntityManager {
            val cb = criteriaBuilder
            val criteria = cb.createQuery(Item::class.java)
            val i = criteria.from(Item::class.java)
            i.fetch<Item, User>("seller")
            criteria.select(i)
            val items = createQuery(criteria).resultList
            close() // detach all
            for (itm in items) {
                assertTrue(itm.seller.username.isNotBlank())
            }
        }
        assertTrue(res2 is Result.Success)
    }

    @Test
    fun fetchBids() = runTest {
        storeTestData()
        val res = entityManagerFactory.withEntityManager {
            val items = createQuery(
                "select i from Item i left join fetch i.bids", Item::class.java
            ).resultList

            // select i.*, b.*
            //  from ITEM i
            //   left outer join BID b on b.ITEM_ID = i.ID
            //  where i.ID = ?
            close() // Detach all
            for (i in items) {
                assertTrue(i.bids.size > 0)
            }
        }
        assertTrue(res is Result.Success)

        val res2 = entityManagerFactory.withEntityManager {
            val cb = criteriaBuilder
            val criteria = cb.createQuery(Item::class.java)
            val root = criteria.from(Item::class.java)
            root.fetch<Item, Bid>("bids", JoinType.LEFT)
            criteria.select(root)
            val items = createQuery(criteria).resultList
            close()
            for (i in items) {
                assertTrue(i.bids.size > 0)
            }
        }
        assertTrue(res2 is Result.Success)
    }
}






















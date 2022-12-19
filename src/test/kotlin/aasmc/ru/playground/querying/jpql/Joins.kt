package aasmc.ru.playground.querying.jpql

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.quering.Bid
import aasmc.ru.playground.quering.Item
import aasmc.ru.playground.quering.LogRecord
import aasmc.ru.playground.quering.User
import aasmc.ru.playground.querying.AbstractQueryTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Joins : AbstractQueryTest() {

    @Test
    fun executeQueries() = runTest {
        val td = storeTestData()
        val res = entityManagerFactory.withEntityManager {
            val implicitInner = createQuery(
                "select b from Bid b where b.item.name like 'Fo%%'",
                Bid::class.java
            )
            val iiResult = implicitInner.resultList
            assertEquals(iiResult.size, 3)
            for (bid in iiResult) {
                assertEquals(bid.item.id, td.items.getFirstId())
            }
            clear()

            val multipleImplicitInner = createQuery(
                "select b from Bid b where b.item.seller.username = 'johndoe'",
                Bid::class.java
            )
            val miiResult = multipleImplicitInner.resultList
            assertEquals(miiResult.size, 4)
            clear()

            val multipleImplicitInnerAnd = createQuery(
                "select b from Bid b where b.item.seller.username = 'johndoe' " +
                        "and b.item.buyNowPrice is not null",
                Bid::class.java
            )
            val miiAndResult = multipleImplicitInnerAnd.resultList
            assertEquals(miiAndResult.size, 3)
            clear()

            val explicitInner = createQuery(
                "select i from Item i join i.bids b where b.amount > 100",
                Item::class.java
            )
            val eResult = explicitInner.resultList
            assertEquals(eResult.size, 1)
            assertEquals(eResult[0].id, td.items.getFirstId())
            clear()

            val explicitOuter = createQuery(
                "select i, b from Item i left join i.bids b on b.amount > 100"
            )
            val eoResult = explicitOuter.resultList as List<Array<Any?>>
            assertEquals(eoResult.size, 3)
            assertTrue(eoResult[0][0] is Item)
            assertTrue(eoResult[0][1] is Bid)
            assertTrue(eoResult[1][0] is Item)
            assertNull(eoResult[1][1])
            assertTrue(eoResult[2][0] is Item)
            assertNull(eoResult[2][1])
            clear()

            val outerFetchCollection = createQuery(
                "select i from Item i left join fetch i.bids",
                Item::class.java
            )
            val outerFetchResult = outerFetchCollection.resultList
            assertEquals(outerFetchResult.size, 3)
            clear()

            val outerFetchMultiple = createQuery(
                "select distinct i from Item i " +
                        "left join fetch i.bids b " +
                        "join fetch b.bidder " +
                        "left join fetch i.seller",
                Item::class.java
            )
            val omResult = outerFetchMultiple.resultList
            assertEquals(omResult.size, 2)
            var haveBids = false
            var haveBidder = false
            var haveSeller = false
            for (item in omResult) {
                detach(item) // No more lazy loading!
                item.bids?.let { bids ->
                    if (bids.size > 0) {
                        haveBids = true
                        val bid = bids.iterator().next()
                        if (bid.bidder != null) {
                            haveBidder = true
                        }
                    }
                }
                if (item.seller != null ) {
                    haveSeller = true
                }
            }
            assertTrue(haveBidder)
            assertTrue(haveSeller)
            assertTrue(haveBids)
            clear()

            // SQL Cartesian product of multiple collections! Bad!
            val badProductFetch = createQuery(
                "select distinct i from Item i " +
                        "left join fetch i.bids " +
                        "left join fetch i.images",
                Item::class.java
            )
            val bpResult = badProductFetch.resultList
            assertEquals(bpResult.size, 3)
            clear()

            val thetaStyle = createQuery(
                "select u, log from User u, LogRecord log " +
                        "where u.username = log.username"
            )
            val tsResult = thetaStyle.resultList as List<Array<Any?>>
            assertEquals(tsResult.size, 2)
            for (row in tsResult) {
                assertTrue(row[0] is User)
                assertTrue(row[1] is LogRecord)
            }
            clear()
        }
        assertTrue(res is Result.Success)
    }

}

package aasmc.ru.playground.concurrency

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.concurrency.version.Bid
import aasmc.ru.playground.concurrency.version.Category
import aasmc.ru.playground.concurrency.version.InvalidBidException
import aasmc.ru.playground.concurrency.version.Item
import aasmc.ru.util.TestData
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import jakarta.persistence.NoResultException
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Versioning : AbstractTest(
    entityProvider = {
        arrayOf(
            Bid::class.java,
            Category::class.java,
            Item::class.java
        )
    }
) {

    @Test
    fun firstCommitWins() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item(name = "Some Item")
            persist(item)
            item.id
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data
        var success: Result<Long>? = null
        val failed = entityManagerFactory.withEntityManager {
            // Retrieving an entity instance by identifier loads the current version
            // from the database with a <code>SELECT</code>.
            val item = find(Item::class.java, id)
            // select * from ITEM where ID = ?
            assertEquals(0, item.getCurrentVersion())
            item.name = "New Name"

            // The concurrent second unit of work doing the same
            withContext(Dispatchers.IO) {
                success = entityManagerFactory.withEntityManager {
                    val concurrentItem = find(Item::class.java, id)
                    assertEquals(0, item.getCurrentVersion())
                    concurrentItem.name = "Other Name"
                    concurrentItem.id
                    // update ITEM set NAME = ?, VERSION = 1 where ID = ? and VERSION = 0
                    // This succeeds, there is a row with ID = ? and VERSION = 0 in the database!
                }
            } // end of concurrent unit of work

            /*
                When the persistence context is flushed Hibernate will detect the dirty
                <code>Item</code> instance and increment its version to 1. The SQL
                <code>UPDATE</code> now performs the version check, storing the new version
                in the database, but only if the database version is still 0.
            */
        }
        assertTrue(failed is Result.Failure)
        assertTrue(success is Result.Success)
    }

    @Test
    fun manualVersionChecking() = runTest {
        val testData = storeCategoriesAndItems()
        val CATEGORIES = testData.categories.identifiers
        var priceString = ""
        entityManagerFactory.withEntityManager {
            var totalPrice = BigDecimal.ZERO
            for (categoryId in CATEGORIES) {
                /*
                   For each <code>Category</code>, query all <code>Item</code> instances with
                   an <code>OPTIMISTIC</code> lock mode. Hibernate now knows it has to
                   check each <code>Item</code> at flush time.
                 */
                val items =
                    createQuery("select i from Item  i where i.category.id = :catId", Item::class.java)
                        .setLockMode(LockModeType.OPTIMISTIC)
                        .setParameter("catId", categoryId)
                        .resultList

                for (item in items) {
                    totalPrice = totalPrice.add(item.buyNowPrice)
                }
                // Now a concurrent transaction will move an item to another category
                if (categoryId == testData.categories.getFirstId()) {
                    withContext(Dispatchers.IO) {
                        entityManagerFactory.withEntityManager {
                            // Moving the first item from the first category into the last category
                            val list =
                                createQuery("select i from Item i where i.category.id = :catId", Item::class.java)
                                    .setParameter("catId", testData.categories.getFirstId())
                                    .resultList
                            val lastCategory = getReference(
                                Category::class.java, testData.categories.getLastId()
                            )
                            list.iterator().next().category = lastCategory
                        }
                    }
                }
            }
            /*
               For each <code>Item</code> loaded earlier with the locking query, Hibernate will
               now execute a <code>SELECT</code> during flushing. It checks if the database
               version of each <code>ITEM</code> row is still the same as when it was loaded
               earlier. If any <code>ITEM</code> row has a different version, or the row doesn't
               exist anymore, an <code>OptimisticLockException</code> will be thrown.
             */
            priceString = totalPrice.toString()
        }
        assertEquals(priceString, "108.00")
    }

    data class ConcurrencyTestData(
        val categories: TestData,
        val items: TestData
    )

    fun storeCategoriesAndItems(): ConcurrencyTestData = runBlocking {
        val result = entityManagerFactory.withEntityManager {
            val testData = ConcurrencyTestData(
                categories = TestData(LongArray(3)),
                items = TestData(LongArray(5))
            )
            for (i in 1..testData.categories.identifiers.size) {
                val category = Category().apply {
                    name = "Category: $i"
                }
                persist(category)
                testData.categories.identifiers[i - 1] = category.id
                for (j in 1..testData.categories.identifiers.size) {
                    val item = Item(name = "Item: $j")
                    item.category = category
                    item.buyNowPrice = BigDecimal(10 + j)
                    persist(item)
                    testData.items.identifiers[(i - 1) + (j - 1)] = item.id
                }
            }
            testData
        }
        assertTrue(result is Result.Success)
        return@runBlocking result.data
    }

    @Test
    fun forceIncrement() = runTest {
        val testData = storeItemAndBids()
        val itemId = testData.getFirstId()
        var success: Result<Unit>? = null
        val failed = entityManagerFactory.withEntityManager {
            /*
               The <code>find()</code> method accepts a <code>LockModeType</code>. The
               <code>OPTIMISTIC_FORCE_INCREMENT</code> mode tells Hibernate that the version
               of the retrieved <code>Item</code> should be incremented after loading,
               even if it's never modified in the unit of work.
             */
            val item = find(Item::class.java, itemId, LockModeType.OPTIMISTIC_FORCE_INCREMENT)
            val highestBid = queryHighestBid(this, item)
            // Now a concurrent transaction will place a bid for this item, and
            // succeed because the first commit wins!
            withContext(Dispatchers.IO) {
                success =  entityManagerFactory.withEntityManager {
                    val itm = find(
                        Item::class.java,
                        testData.getFirstId(),
                        LockModeType.OPTIMISTIC_FORCE_INCREMENT
                    )
                    val highest = queryHighestBid(this, itm)
                    try {
                        val newBid = Bid(
                            amount = BigDecimal("44.44"),
                            item = itm,
                            lastBid = highest
                        )
                        persist(newBid)
                    } catch (e: InvalidBidException) {
                        // ignore
                    }
                }
            }

            try {
                val newBid = Bid(
                    amount = BigDecimal("44.44"),
                    item = item,
                    lastBid = highestBid
                )
                persist(newBid)
            } catch (e: InvalidBidException) {
                // Bid too low, show a validation error screen...
            }

            /*
                When flushing the persistence context, Hibernate will execute an
                <code>INSERT</code> for the new <code>Bid</code> and force an
                <code>UPDATE</code> of the <code>Item</code> with a version check.
                If someone modified the <code>Item</code> concurrently, or placed a
                <code>Bid</code> concurrently with this procedure, Hibernate throws
                an exception.
              */
        }
        assertTrue(failed is Result.Failure)
        assertTrue(success is Result.Success)
    }

    fun storeItemAndBids(): TestData = runBlocking {
        val result = entityManagerFactory.withEntityManager {
            val ids = LongArray(1)
            val item = Item(name = "Some Item")
            persist(item)
            ids[0] = item.id
            for (i in 1..3) {
                val bid = Bid(amount = BigDecimal(10 + i), item = item)
                persist(bid)
            }
            TestData(ids)
        }
        assertTrue(result is Result.Success)
        return@runBlocking result.data
    }

    fun queryHighestBid(em: EntityManager, item: Item): Bid? {
        // Can't scroll with cursors in JPA, have to use setMaxResult()
        return try {
            em.createQuery(
                "select b from Bid b " +
                        "where b.item = :itm " +
                        "order by b.amount desc", Bid::class.java
            )
                .setParameter("itm", item)
                .setMaxResults(1)
                .singleResult
        } catch (e: NoResultException) {
            null
        }
    }

}
























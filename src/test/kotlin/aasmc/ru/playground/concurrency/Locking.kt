package aasmc.ru.playground.concurrency

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.concurrency.version.Category
import aasmc.ru.playground.concurrency.version.Item
import jakarta.persistence.LockModeType
import jakarta.persistence.PessimisticLockScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class Locking: Versioning() {
    @Test
    fun pessimisticReadWrite() = runTest {
        val testData = storeCategoriesAndItems()
        val categories = testData.categories.identifiers
        var resultPrice: BigDecimal = BigDecimal.ZERO
        var failed: Result<Unit>? = null
        val success = entityManagerFactory.withEntityManager {
            var totalPrice = BigDecimal(0)
            for (categoryId in categories) {
                /*
                   For each <code>Category</code>, query all <code>Item</code> instances in
                   <code>PESSIMISTIC_READ</code> lock mode. Hibernate will lock the rows in
                   the database with the SQL query. If possible, wait for 5 seconds if some
                   other transaction already holds a conflicting lock. If the lock can't
                   be obtained, the query throws an exception.
                 */
                val items =
                    createQuery("select i from Item i where i.category.id = :catId", Item::class.java)
                        .setLockMode(LockModeType.PESSIMISTIC_READ) // ensures Repeatable Read isolation
                        .setHint("jakarta.persistence.lock.timeout", 5000)
                        .setParameter("catId", categoryId)
                        .resultList

                /*
                   If the query returns successfully, you know that you hold an exclusive lock
                   on the data and no other transaction can access it with an exclusive lock or
                   modify it until this transaction commits.
                 */
                for (itm in items) {
                    totalPrice = totalPrice.add(itm.buyNowPrice)
                }
                // Now a concurrent transaction will try to obtain a write lock, it fails because
                // we hold a read lock on the data already. Note that on H2 there actually are no
                // read or write locks, only exclusive locks.
                if (categoryId == testData.categories.getFirstId()) {
                    withContext(Dispatchers.IO) {
                        failed = entityManagerFactory.withEntityManager {
                            // Moving the first item from the first category into the last category
                            // This query should fail as someone else holds a lock on the rows.
                            val itms =
                                createQuery("select i from Item i where i.category.id = :catId", Item::class.java)
                                    .setParameter("catId", testData.categories.getFirstId())
                                    .setLockMode(LockModeType.PESSIMISTIC_WRITE) // Prevent concurrent access
                                    .setHint("jakarta.persistence.lock.timeout", 5000)
                                    .resultList

                            val lastCategory = getReference(Category::class.java, testData.categories.getLastId())
                            itms.iterator().next().category = lastCategory
                        }
                    }
                }
            }

            resultPrice = totalPrice
        }
        assertTrue(failed is Result.Failure)
        assertTrue(success is Result.Success)
        assertTrue(resultPrice.compareTo(BigDecimal("108")) == 0)
    }

    @Test
    fun findLock() = runTest {
        val testData = storeCategoriesAndItems()
        val id = testData.categories.getFirstId()
        val success = entityManagerFactory.withEntityManager {
            val hints = hashMapOf<String, Any?>()
            hints["jakarta.persistence.lock.timeout"] = 5000
            // Executes a SELECT ... FOR UPDATE WAIT 5000 if supported by dialect
            val category =
                find(
                    Category::class.java,
                    id,
                    LockModeType.PESSIMISTIC_WRITE,
                    hints
                )
            category.name = "New Name"
        }
        assertTrue(success is Result.Success)
    }

    @Test
    fun extendedLock() = runTest {
        val testData = storeCategoriesAndItems()
        val id = testData.categories.getFirstId()
        val result = entityManagerFactory.withEntityManager {
            val hints = hashMapOf<String, Any?>()
            hints["jakarta.persistence.lock.scope"] = PessimisticLockScope.EXTENDED
            val item = find(
                Item::class.java,
                id,
                LockModeType.PESSIMISTIC_READ,
                hints
            )
            assertEquals(0, item.images.size)
        }
        assertTrue(result is Result.Success)
    }
}

















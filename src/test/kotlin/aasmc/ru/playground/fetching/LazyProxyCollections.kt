package aasmc.ru.playground.fetching

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.fetching.proxy.Bid
import aasmc.ru.playground.fetching.proxy.Category
import aasmc.ru.playground.fetching.proxy.Item
import aasmc.ru.playground.fetching.proxy.User
import aasmc.ru.util.TestData
import io.ktor.client.plugins.*
import jakarta.persistence.Persistence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hibernate.Hibernate
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class LazyProxyCollections: AbstractTest(
    entityProvider = {
        arrayOf(
            Bid::class.java,
            Category::class.java,
            Item::class.java,
            User::class.java
        )
    }
) {

    private lateinit var loadEventListener: FetchLoadEventListener

    override fun afterJpaBootStrap() {
        loadEventListener = FetchLoadEventListener(entityManagerFactory)
    }

    private fun storeTestData(): FetchTestData = runBlocking {
        val res = entityManagerFactory.withEntityManager {
            val categoryIds = LongArray(3)
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

            val category = Category(name = "Category One")
            persist(category)
            categoryIds[0] = category.id!!

            val item = Item(
                name = "Item One",
                auctionEnd = LocalDate.now().plusDays(1),
                seller = johnDoe
            )
            persist(item)
            itemIds[0] = item.id!!
            category.items.add(item)
            item.categories.add(category)
            for (i in 1..3) {
                val bid = Bid(item = item, bidder = robDoe, amount = BigDecimal(9 + i))
                item.bids.add(bid)
                persist(bid)
            }
            val categoryTwo = Category(name = "Category Two")
            persist(categoryTwo)
            categoryIds[1] = categoryTwo.id!!

            val itemTwo = Item(
                name = "Item Two",
                auctionEnd = LocalDate.now().plusDays(1),
                seller = robDoe
            )
            persist(itemTwo)
            itemIds[1] = itemTwo.id!!
            categoryTwo.items.add(itemTwo)
            itemTwo.categories.add(categoryTwo)
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
            categoryTwo.items.add(item)
            itemThree.categories.add(categoryTwo)

            val categoryThree = Category(name = "Category Three")
            persist(categoryThree)
            categoryIds[2] = categoryThree.id!!

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
    fun lazyEntityProxies() = runTest {
        val testData = storeTestData()

        val res = entityManagerFactory.withEntityManager {
            val itemId = testData.items!!.getFirstId()

            val item = getReference(Item::class.java, itemId) // No SELECT

            // Calling identifier getter (no field access!) doesn't trigger initialization
            assertEquals(itemId, item.id)

            // The class is runtime generated, named something like: Item_$$_javassist_1
            assertNotEquals(Item::class.java, item.javaClass)

            assertEquals(
                Item::class.java,
                HibernateProxyHelper.getClassWithoutInitializingProxy(item)
            )
            val persistenceUtil = Persistence.getPersistenceUtil()
            assertFalse(persistenceUtil.isLoaded(item))
            assertFalse(persistenceUtil.isLoaded(item, "seller"))

            assertFalse(Hibernate.isInitialized(item))
            // Would trigger initialization of item!
            // assertFalse(Hibernate.isInitialized(item.getSeller()));
            Hibernate.initialize(item)
            // select * from ITEM where ID = ?

            // Let's make sure the default EAGER of @ManyToOne has been overriden with LAZY
            assertFalse(Hibernate.isInitialized(item.seller))

            Hibernate.initialize(item.seller)
            // select * from USERS where ID = ?
        }
        assertTrue(res is Result.Success)

        val res2 = entityManagerFactory.withEntityManager {
            val itemId = testData.items!!.getFirstId()
            val userId = testData.users!!.getFirstId()

            /*
              An <code>Item</code> entity instance is loaded in the persistence context, its
              <code>seller</code> is not initialized, it's a <code>User</code> proxy.
            */
            val item = find(Item::class.java, itemId)
            // select * from ITEM where ID = ?

            /*
               You can manually detach the data from the persistence context, or close the
               persistence context and detach everything.
             */
            detach(item)
            detach(item.seller)

            val persistenceUtil = Persistence.getPersistenceUtil()
            assertTrue(persistenceUtil.isLoaded(item))
            assertFalse(persistenceUtil.isLoaded(item, "seller"))

            /*
              In detached state, you can call the identifier getter method of the
              <code>User</code> proxy. However, calling any other method on the proxy,
              such as <code>getUsername()</code>, will throw a <code>LazyInitializationException</code>.
              Data can only be loaded on-demand while the persistence context manages the proxy, not in detached
              state.
            */
            assertEquals(item.seller.id, userId)
            // Throws exception!
//            assertNotNull(item.seller.username)
        }
        assertTrue(res2 is Result.Success)

        val res3 = entityManagerFactory.withEntityManager {
            val itemId = testData.items!!.getFirstId()
            val userId = testData.users!!.getFirstId()

            val item = getReference(Item::class.java, itemId)
            val user = getReference(User::class.java, userId)
            val newBid = Bid(amount = BigDecimal("99.00"))
            newBid.item = item
            newBid.bidder = user
            persist(newBid)
            // insert into BID values (?, ? ,? , ...)
            flush()
            clear()
            assertEquals(find(Bid::class.java, newBid.id).amount.compareTo(BigDecimal("99")), 0)
        }
        assertTrue(res3 is Result.Success)
    }

    @Test
    fun lazyCollections() = runTest {
        val testData = storeTestData()
        val res = entityManagerFactory.withEntityManager {
            val itemId = testData.items!!.getFirstId()

            val item = find(Item::class.java, itemId)
            // select * from ITEM where ID = ?

            val bids = item.bids // collection is not initialized
            val persistenceUtil = Persistence.getPersistenceUtil()
            assertFalse(persistenceUtil.isLoaded(item, "bids"))

            // it's a set but not a HashSet, it is a PersistentSet
            assertTrue(Set::class.java.isAssignableFrom(bids.javaClass))

            assertEquals(3, item.bids.size)
            // since we use:
            // @org.hibernate.annotations.LazyCollection(
            //     org.hibernate.annotations.LazyCollectionOption.EXTRA
            // )
            // there will be no initialisation of bids
            // select count(b) from BID b where b.ITEM_ID = ?

            val firstBid = bids.iterator().next()
            // select * from BID where ITEM_ID = ?
            // Alternative: Hibernate.initialize(bids);

        }

        assertTrue(res is Result.Success)
    }

    @Test
    fun nPlusOneSelectProblem() = runTest {
        val td = storeTestData()
        loadEventListener.reset()
        val res = entityManagerFactory.withEntityManager {
            val items = createQuery("select i from Item i", Item::class.java).resultList
            // select * from ITEM
            assertEquals(loadEventListener.getLoadCount(Item::class.java), 3)
            assertEquals(loadEventListener.getLoadCount(User::class.java), 0)

            for (i in items) {
                // Each seller has to be loaded with an additional SELECT
                assertTrue(i.seller.username.isNotBlank())
                // select * from USERS where ID = ?
            }
            assertEquals(3, loadEventListener.getLoadCount(User::class.java))
        }
        assertTrue(res is Result.Success)

        val res2 = entityManagerFactory.withEntityManager {
            loadEventListener.reset()
            val items = createQuery("select i from Item i", Item::class.java).resultList
            // select * from ITEM
            assertEquals(loadEventListener.getLoadCount(Item::class.java), 3)
            assertEquals(loadEventListener.getLoadCount(Bid::class.java), 0)

            for (i in items) {
                // Each bids collection has to be loaded with an additional SELECT
                for (b in i.bids) {
                    assertNotNull(b.item)
                }
                // select * from BID where ITEM_ID = ?
            }
            assertEquals(4, loadEventListener.getLoadCount(Bid::class.java))
        }
        assertTrue(res2 is Result.Success)
    }


}

























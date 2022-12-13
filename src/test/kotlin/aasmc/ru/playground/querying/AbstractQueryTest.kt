package aasmc.ru.playground.querying

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.inheritance.tableperclass.BankAccount
import aasmc.ru.playground.inheritance.tableperclass.BillingDetails
import aasmc.ru.playground.inheritance.tableperclass.CreditCard
import aasmc.ru.playground.quering.*
import aasmc.ru.util.TestData
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertTrue

abstract class AbstractQueryTest: AbstractTest(
    entityProvider = {
        arrayOf(
            Bid::class.java,
            Category::class.java,
            Item::class.java,
            LogRecord::class.java,
            User::class.java,
            CreditCard::class.java,
            BillingDetails::class.java,
            BankAccount::class.java
        )
    }
) {

    fun storeTestData(): TestDataCategoriesItems = runBlocking {
        val res = entityManagerFactory.withEntityManager {
            val categoryIds = LongArray(4)
            val itemIds = LongArray(3)
            val userIds = LongArray(3)
            val johndoe = User(username = "johndoe", firstname = "John", lastname = "Doe")
            val homeAddress = Address("Some Street 123", "12345", "Some City")
            johndoe.activated = true
            johndoe.address = homeAddress
            persist(johndoe)
            userIds[0] = johndoe.id!!

            val janeroe = User(username = "janeroe", firstname = "Jane", lastname = "Roe")
            janeroe.activated = true
            janeroe.address  = Address("Other Street 11", "1234", "Other City")
            persist(janeroe)
            userIds[1] = janeroe.id!!

            val robertdoe = User(username = "robertdoe", firstname = "Robert", lastname = "Doe")
            persist(robertdoe)
            userIds[2] = robertdoe.id!!

            val categoryOne = Category(name = "One")
            persist(categoryOne)
            categoryIds[0] = categoryOne.id!!

            var item = Item(name = "Foo", auctionEnd = LocalDate.now().plusDays(1), seller = johndoe)
            item.buyNowPrice = BigDecimal("19.99")
            persist(item)
            itemIds[0] = item.id!!
            categoryOne.items.add(item)
            item.categories.add(categoryOne)
            for (i in 1..3) {
                val bid = Bid(item = item, user = robertdoe, amount = BigDecimal(98 + i))
                item.bids.add(bid)
                persist(bid)
            }
            item.images.add(Image("Foo", "foo.jpg", 640, 480))
            item.images.add(Image("Bar", "bar.jpg", 800, 600))
            item.images.add(Image("Baz", "baz.jpg", 1024, 768))

            val categoryTwo = Category(name = "Two")
            categoryTwo.parent = categoryOne
            persist(categoryTwo)
            categoryIds[1] = categoryTwo.id!!

            item = Item(name = "Bar", auctionEnd = LocalDate.now().plusDays(1), seller = johndoe)
            persist(item)
            itemIds[1] = item.id!!
            categoryTwo.items.add(item)
            item.categories.add(categoryTwo)
            val bid = Bid(item = item, user = janeroe, amount = BigDecimal("4.99"))
            item.bids.add(bid)
            persist(bid)

            item = Item(name = "Baz", auctionEnd = LocalDate.now().plusDays(2), seller = janeroe)
            item.approved = false
            persist(item)
            itemIds[2] = item.id!!
            categoryTwo.items.add(item)
            item.categories.add(categoryTwo)

            val categoryThree = Category(name = "Three")
            categoryThree.parent = categoryOne
            persist(categoryThree)
            categoryIds[2] = categoryThree.id!!

            val categoryFour = Category(name = "Four")
            categoryFour.parent = categoryTwo
            persist(categoryFour)
            categoryIds[3] = categoryFour.id!!

            val cc = CreditCard(
                "John Doe", "1234123412341234", "06", "2015"
            )
            persist(cc)

            val ba = BankAccount(
                "Jane Roe", "445566", "One Percent Bank Inc.", "999"
            )
            persist(ba)

            var lr = LogRecord(username = "johndoe", msg = "This is a log message")
            persist(lr)
            lr = LogRecord(username = "johndoe", msg = "Another log message")
            persist(lr)

            TestDataCategoriesItems(
                categories = TestData(categoryIds),
                items = TestData(itemIds),
                users = TestData(userIds)
            )
        }
        assertTrue(res is Result.Success)
        res.data
    }
}

data class TestDataCategoriesItems(
    val categories: TestData,
    val items: TestData,
    val users: TestData
)




















package aasmc.ru.playground.model

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.copy
import jakarta.validation.Validation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hibernate.Session
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class ItemTest : AbstractTest() {

    @Test
    fun validateItem() {
        val factory = Validation.buildDefaultValidatorFactory()
        val validator = factory.validator
        val item = Item()
        item.name = "Some Name"
        item.auctionEnd = Instant.now()

        val violations = validator.validate(item)
        // We have one validation error, auction end date was not in the future!
        assertEquals(1, violations.size)
        val violation = violations.iterator().next()
        val failedPropertyName = violation.propertyPath.iterator().next().name
        assertEquals("auctionEnd", failedPropertyName)
        if (Locale.getDefault().language == "en") {
            assertEquals("must be a future date", violation.message)
        }
    }

    @Test
    fun metricImperialTransformerExampleTest() = runTest {
        storeItemsAndBids()
        entityManagerFactory.withEntityManager {

            // NOTE that database probably won't be able to rely on an index for this
            // restriction, we'll encounter a full table scan, because the weight for all
            // ITEM rows has to be calculated to evaluate the restriction.

            val result = createQuery(
                "select i from Item i where i.metricWeight = :w",
                Item::class.java
            ).setParameter("w", 2.0).resultList
            assertEquals(1, result.size)
            assertEquals("Some item", result[0].name)
        }
    }

    @Test
    fun storeLoadProperties() = runTest {
        val idResult = entityManagerFactory.withEntityManager {
            val someItem = Item()
            someItem.name = "Some Item"
            someItem.description = "This is some description"
            val bytes = ByteArray(131072)
            Random().nextBytes(bytes)
            someItem.image = bytes
            persist(someItem)
            someItem.getId()
        }
        advanceUntilIdle()
        assertTrue(idResult is Result.Success)
        val id = (idResult as Result.Success).data!!
        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)
            assertEquals("This is some description", item.description)
            assertEquals(131072, item.image!!.size)
        }
    }

    @Test
    fun storeLoadLOB() = runTest {
        val idResult = entityManagerFactory.withEntityManager {
            val someItem = Item()
            someItem.name = "Some Item"
            someItem.description = "This is some description"

            val bytes = ByteArray(131072)
            Random().nextBytes(bytes)
            val imageInputStream = ByteArrayInputStream(bytes)
            val byteLength = bytes.size.toLong()

            val session = unwrap(Session::class.java)
            val blob = session.lobHelper
                .createBlob(imageInputStream, byteLength)

            someItem.imageBlog = blob
            persist(someItem)
            someItem.getId()
        }

        advanceUntilIdle()
        assertTrue(idResult is Result.Success)
        val id = (idResult as Result.Success).data!!

        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)
            // you can stream the bytes directly
            val imageDataStream = item.imageBlog!!.binaryStream

            // or materialize them into memory
            val outStream = ByteArrayOutputStream()
            copy(imageDataStream, outStream)
            val imageBytes = outStream.toByteArray()
            assertEquals(imageBytes.size, 131072)
        }
    }

    @Test
    fun testMonetaryAmountCompositeType() = runTest {
        val idResult = entityManagerFactory.withEntityManager {
            val someItem = Item()
            someItem.name = "Some Item"
            someItem.description = "This is some description"
            someItem.customPrice = MonetaryAmount(
                value = BigDecimal.ONE,
                currency = Currency.getInstance("EUR")
            )
            persist(someItem)
            someItem.getId()
        }
        advanceUntilIdle()
        assertTrue(idResult is Result.Success)
        val id = (idResult as Result.Success).data!!
        val res = entityManagerFactory.withEntityManager {
            find(Item::class.java, id)
        }

        advanceUntilIdle()
        assertTrue(res is Result.Success)
        val item = (res as Result.Success).data
        assertTrue(BigDecimal.ONE.compareTo(item.customPrice!!.value) == 0)
        assertEquals(item.customPrice!!.currency.currencyCode, "EUR")
    }

    private fun storeItemsAndBids() = runBlocking {
        entityManagerFactory.withEntityManager {
            val item = Item()
            item.name = "Some item"
            item.description = "This is some description"
            item.metricWeight = 2.0
            persist(item)

            val item2 = Item()
            item2.name = "Second item"
            item2.description = "This is some second description"
            item2.metricWeight = 3.0
            persist(item2)
        }
    }
}


















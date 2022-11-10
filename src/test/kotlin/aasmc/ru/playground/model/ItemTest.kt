package aasmc.ru.playground.model

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import jakarta.validation.Validation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.*

internal class ItemTest: AbstractTest() {

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

    @OptIn(ExperimentalCoroutinesApi::class)
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
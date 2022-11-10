package aasmc.ru.playground.simple

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

internal class ItemTest {

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
}
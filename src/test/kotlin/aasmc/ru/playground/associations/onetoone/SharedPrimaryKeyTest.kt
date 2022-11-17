package aasmc.ru.playground.associations.onetoone

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.associations.onetoone.sharedprimarykey.Address
import aasmc.ru.playground.associations.onetoone.sharedprimarykey.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SharedPrimaryKeyTest: AbstractTest(
    entityProvider = {
        arrayOf(
            User::class.java,
            Address::class.java
        )
    }
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun storeAndLoadUserAddress() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val address = Address(
                street = "Some Street 123", zipcode = "12345", city = "Some City"
            )
            persist(address) // Generate identifier value
            val user = User(
                id = address.id,
                username = "JohnDoe"
            )
            persist(user)
            user.shippingAddress = address // optional
            user.id to address.id
        }

        assertTrue(idRes is Result.Success)
        val pair = idRes.data
        val userId = pair.first
        val addressId = pair.second

        assertEquals(userId, addressId)

        entityManagerFactory.withEntityManager {
            val user = find(User::class.java, userId)
            assertEquals("12345", user.shippingAddress!!.zipcode)

            val address = find(Address::class.java, addressId)
            assertEquals("12345", address.zipcode)
            assertEquals(user.id, address.id)
        }
    }
}























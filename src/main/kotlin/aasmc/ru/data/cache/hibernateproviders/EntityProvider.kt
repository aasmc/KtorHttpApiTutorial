package aasmc.ru.data.cache.hibernateproviders

import aasmc.ru.data.cache.models.CachedCustomer
import aasmc.ru.data.cache.models.CachedItem
import aasmc.ru.data.cache.models.CachedOrder
import aasmc.ru.data.cache.models.CachedUser
import aasmc.ru.playground.simple.Bid
import aasmc.ru.playground.simple.Category
import aasmc.ru.playground.simple.Item
import aasmc.ru.playground.simple.User

object EntityProvider {
    fun provideEntities(forTest: Boolean): Array<Class<*>> {
        return if (forTest) {
            testEntities()
        } else {
            appEntities()
        }
    }

    private fun appEntities(): Array<Class<*>> {
        return arrayOf(
            CachedCustomer::class.java,
            CachedOrder::class.java,
            CachedItem::class.java,
            CachedUser::class.java
        )
    }

    private fun testEntities(): Array<Class<*>> {
        return arrayOf(
            Item::class.java,
            Bid::class.java,
            Category::class.java,
            User::class.java
        )
    }
}
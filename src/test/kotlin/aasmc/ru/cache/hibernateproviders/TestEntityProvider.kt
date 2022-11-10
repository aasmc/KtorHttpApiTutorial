package aasmc.ru.cache.hibernateproviders

import aasmc.ru.data.cache.hibernateproviders.interfaces.EntityProvider
import aasmc.ru.playground.simple.Bid
import aasmc.ru.playground.simple.Category
import aasmc.ru.playground.simple.Item
import aasmc.ru.playground.simple.User

class TestEntityProvider: EntityProvider {
    override fun provideEntities(): Array<Class<*>> {
        return arrayOf(
            Item::class.java,
            Bid::class.java,
            Category::class.java,
            User::class.java
        )
    }
}
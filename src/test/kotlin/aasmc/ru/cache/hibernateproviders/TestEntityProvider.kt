package aasmc.ru.cache.hibernateproviders

import aasmc.ru.data.cache.hibernateproviders.interfaces.EntityProvider
import aasmc.ru.playground.simple.*

class TestEntityProvider: EntityProvider {
    override fun provideEntities(): Array<Class<*>> {
        return arrayOf(
            Item::class.java,
            Bid::class.java,
            Category::class.java,
            User::class.java,
        )
    }
}
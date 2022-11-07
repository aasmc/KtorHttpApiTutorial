package aasmc.ru.data.cache.hibernateproviders

import aasmc.ru.data.cache.models.CachedCustomer
import aasmc.ru.data.cache.models.CachedItem
import aasmc.ru.data.cache.models.CachedOrder
import aasmc.ru.data.cache.models.CachedUser

object EntityProvider {
    fun provideEntities(): Array<Class<*>> {
        return arrayOf(
            CachedCustomer::class.java,
            CachedOrder::class.java,
            CachedItem::class.java,
            CachedUser::class.java
        )
    }
}
package aasmc.ru.data.cache.ehcache

import aasmc.ru.data.cache.models.CachedCustomer
import aasmc.ru.data.cache.models.CachedOrder
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.config.units.MemoryUnit
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration
import java.io.File

class CacheManager(
    storagePath: File
) {
    companion object {
        private const val ORDERS_CACHE_KEY = "ordersCache"
        private const val CUSTOMERS_CACHE_KEY = "ordersCache"
    }

    private val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerPersistenceConfiguration(storagePath))
        .withCache(
            ORDERS_CACHE_KEY,
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Long::class.javaObjectType,
                CachedOrder::class.java,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(1000, EntryUnit.ENTRIES)
                    .offheap(10, MemoryUnit.MB)
                    .disk(100, MemoryUnit.MB, true)
            )
        )
        .withCache(
            CUSTOMERS_CACHE_KEY,
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String::class.javaObjectType,
                CachedCustomer::class.java,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(1000, EntryUnit.ENTRIES)
                    .offheap(10, MemoryUnit.MB)
                    .disk(100, MemoryUnit.MB, true)
            )
        ).build(true)

    fun getCustomersCache() =
        cacheManager.getCache(
            CUSTOMERS_CACHE_KEY,
            String::class.javaObjectType,
            CachedCustomer::class.java
        )

    fun getOrdersCache() =
        cacheManager.getCache(
            ORDERS_CACHE_KEY,
            Long::class.javaObjectType,
            CachedOrder::class.java
        )
}
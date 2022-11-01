package aasmc.ru.database.dao

import aasmc.ru.models.Customer
import aasmc.ru.models.Order
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.config.units.MemoryUnit
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration
import java.io.File

class DAOFacadeCacheImpl(
    private val delegate: DAOFacade,
    storagePath: File,
): DAOFacade {
    private val cacheManager =
        CacheManagerBuilder.newCacheManagerBuilder()
            .with(CacheManagerPersistenceConfiguration(storagePath))
            .withCache(
                "ordersCache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    String::class.javaObjectType,
                    Order::class.java,
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(1000, EntryUnit.ENTRIES)
                        .offheap(10, MemoryUnit.MB)
                        .disk(100, MemoryUnit.MB, true)
                )
            )
            .withCache(
                "customerCache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    Int::class.javaObjectType,
                    Customer::class.java,
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(1000, EntryUnit.ENTRIES)
                        .offheap(10, MemoryUnit.MB)
                        .disk(100, MemoryUnit.MB, true)
                )
            ).build(true)

    private val customersCache = cacheManager.getCache("customerCache", Int::class.javaObjectType, Customer::class.java)
    private val orderCache = cacheManager.getCache("ordersCache", String::class.javaObjectType, Order::class.java)

    override suspend fun allCustomers(): List<Customer> =
        delegate.allCustomers()

    override suspend fun customer(id: String): Customer? =
        customersCache[id.toInt()] ?: delegate.customer(id).also { saved ->
            saved?.let {
                customersCache.put(id.toInt(), it)
            }
        }

    override suspend fun addNewCustomer(customer: Customer): Customer =
        delegate.addNewCustomer(customer).also { saved ->
            customersCache.put(saved.id.toInt(), saved)
        }

    override suspend fun deleteCustomer(id: String): Boolean {
        customersCache.remove(id.toInt())
        return delegate.deleteCustomer(id)
    }

    override suspend fun hasOrders(): Boolean =
        delegate.hasOrders()

    override suspend fun addNewOrder(order: Order): Order =
        delegate.addNewOrder(order).also {
            orderCache.put(order.number, it)
        }

    override suspend fun allOrders(): List<Order> =
        delegate.allOrders()

    override suspend fun order(number: String): Order? =
        orderCache.get(number) ?: delegate.order(number).also { saved ->
            saved?.let {
                orderCache.put(number, it)
            }
        }

    override suspend fun totalAmountForOrder(number: String): Double =
        orderCache.get(number)?.contents?.sumOf { it.price * it.amount }
            ?: delegate.totalAmountForOrder(number)
}
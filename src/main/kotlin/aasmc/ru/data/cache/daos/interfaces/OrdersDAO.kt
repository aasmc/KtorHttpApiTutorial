package aasmc.ru.data.cache.daos.interfaces

import aasmc.ru.data.cache.models.CachedOrder

interface OrdersDAO {
    suspend fun hasOrders(): Boolean
    suspend fun addNewOrder(order: CachedOrder): Boolean
    suspend fun allOrders(): List<CachedOrder>
    suspend fun order(number: String): CachedOrder?
    suspend fun totalAmountForOrder(number: String): Double
}
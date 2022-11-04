package aasmc.ru.domain.repositories

import aasmc.ru.domain.model.Order

interface OrdersRepository {
    suspend fun hasOrders(): Boolean
    suspend fun addNewOrder(order: Order): Boolean
    suspend fun allOrders(): List<Order>
    suspend fun order(number: String): Order?
    suspend fun totalAmountForOrder(number: String): Double
}
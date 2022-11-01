package aasmc.ru.database.dao

import aasmc.ru.models.Customer
import aasmc.ru.models.Order

interface DAOFacade {
    suspend fun allCustomers(): List<Customer>
    suspend fun customer(id: String): Customer?
    suspend fun addNewCustomer(customer: Customer): Customer
    suspend fun deleteCustomer(id: String): Boolean

    suspend fun hasOrders(): Boolean
    suspend fun addNewOrder(order: Order): Order
    suspend fun allOrders(): List<Order>
    suspend fun order(number: String): Order?
    suspend fun totalAmountForOrder(number: String): Double
}
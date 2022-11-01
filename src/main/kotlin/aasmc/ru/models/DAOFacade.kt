package aasmc.ru.models

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
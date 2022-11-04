package aasmc.ru.domain.repositories

import aasmc.ru.domain.model.Customer

interface CustomersRepository {
    suspend fun allCustomers(): List<Customer>
    suspend fun customer(id: String): Customer?
    suspend fun addNewCustomer(customer: Customer): Boolean?
    suspend fun deleteCustomer(id: String): Boolean
}
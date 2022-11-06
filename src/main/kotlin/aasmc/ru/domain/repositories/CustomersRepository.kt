package aasmc.ru.domain.repositories

import aasmc.ru.domain.model.Customer
import aasmc.ru.domain.model.Result

interface CustomersRepository {
    suspend fun allCustomers(): Result<List<Customer>>
    suspend fun customer(id: String): Result<Customer?>
    suspend fun addNewCustomer(customer: Customer): Result<Unit>
    suspend fun deleteCustomer(id: String): Result<Unit>
}
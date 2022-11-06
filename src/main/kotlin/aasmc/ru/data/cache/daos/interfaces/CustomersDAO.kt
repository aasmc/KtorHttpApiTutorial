package aasmc.ru.data.cache.daos.interfaces

import aasmc.ru.data.cache.models.CachedCustomer
import aasmc.ru.domain.model.Result

interface CustomersDAO {
    suspend fun allCustomers(): Result<List<CachedCustomer>>
    suspend fun customer(id: String): Result<CachedCustomer?>
    suspend fun addNewCustomer(customer: CachedCustomer): Result<Unit>
    suspend fun deleteCustomer(id: String): Result<Unit>
}
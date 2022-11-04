package aasmc.ru.data.cache.daos.interfaces

import aasmc.ru.data.cache.models.CachedCustomer

interface CustomersDAO {
    suspend fun allCustomers(): List<CachedCustomer>
    suspend fun customer(id: String): CachedCustomer?
    suspend fun addNewCustomer(customer: CachedCustomer): Boolean?
    suspend fun deleteCustomer(id: String): Boolean
}
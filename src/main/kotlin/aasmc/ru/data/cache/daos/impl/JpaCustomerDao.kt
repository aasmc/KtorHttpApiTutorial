package aasmc.ru.data.cache.daos.impl

import aasmc.ru.data.cache.daos.interfaces.CustomersDAO
import aasmc.ru.data.cache.models.CachedCustomer
import aasmc.ru.domain.model.Result
import jakarta.persistence.EntityManagerFactory

class JpaCustomerDao(
    private val entityManagerFactory: EntityManagerFactory
): CustomersDAO {
    override suspend fun allCustomers(): Result<List<CachedCustomer>> {
        TODO("Not yet implemented")
    }

    override suspend fun customer(id: String): Result<CachedCustomer?> {
        TODO("Not yet implemented")
    }

    override suspend fun addNewCustomer(customer: CachedCustomer): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCustomer(id: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}
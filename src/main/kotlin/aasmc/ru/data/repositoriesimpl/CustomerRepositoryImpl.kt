package aasmc.ru.data.repositoriesimpl

import aasmc.ru.data.cache.daos.impl.jpa.JpaCustomerDao
import aasmc.ru.data.cache.daos.impl.nativehibernate.HibernateCustomersDao
import aasmc.ru.data.cache.daos.interfaces.CustomersDAO
import aasmc.ru.data.cache.hibernateproviders.HibernateFactory
import aasmc.ru.data.cache.models.mappers.CustomerMapper
import aasmc.ru.domain.model.Customer
import aasmc.ru.domain.model.Result
import aasmc.ru.domain.repositories.CustomersRepository
import io.ktor.server.config.*

class CustomerRepositoryImpl(
    private val config: ApplicationConfig,
    private val customersDAO: CustomersDAO = JpaCustomerDao(HibernateFactory.createEntityManagerFactory(config)),
    private val customersMapper: CustomerMapper = CustomerMapper()
) : CustomersRepository {

    override suspend fun allCustomers(): Result<List<Customer>> {
        return when(val result = customersDAO.allCustomers()) {
            is Result.Failure -> result
            is Result.Success -> Result.Success(result.data.map(customersMapper::mapToDomain))
        }
    }

    override suspend fun customer(id: String): Result<Customer?> {
        return when(val result = customersDAO.customer(id)) {
            is Result.Failure -> result
            is Result.Success -> Result.Success(result.data?.let { customersMapper.mapToDomain(it) })
        }
    }

    override suspend fun addNewCustomer(customer: Customer): Result<Unit> =
        customersDAO.addNewCustomer(customersMapper.mapToEntity(customer))

    override suspend fun deleteCustomer(id: String): Result<Unit> =
        customersDAO.deleteCustomer(id)
}
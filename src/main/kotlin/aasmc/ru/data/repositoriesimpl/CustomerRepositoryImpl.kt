package aasmc.ru.data.repositoriesimpl

import aasmc.ru.data.cache.daos.impl.HibernateCustomersDao
import aasmc.ru.data.cache.daos.interfaces.CustomersDAO
import aasmc.ru.data.cache.models.mappers.CustomerMapper
import aasmc.ru.domain.model.Customer
import aasmc.ru.domain.repositories.CustomersRepository

class CustomerRepositoryImpl(
    private val customersDAO: CustomersDAO = HibernateCustomersDao(),
    private val customersMapper: CustomerMapper = CustomerMapper()
) : CustomersRepository {
    override suspend fun allCustomers(): List<Customer> =
        customersDAO.allCustomers().map(customersMapper::mapToDomain)

    override suspend fun customer(id: String): Customer? =
        customersDAO.customer(id)?.let { customersMapper.mapToDomain(it) }

    override suspend fun addNewCustomer(customer: Customer): Boolean? =
        customersDAO.addNewCustomer(customersMapper.mapToEntity(customer))

    override suspend fun deleteCustomer(id: String): Boolean =
        customersDAO.deleteCustomer(id)
}
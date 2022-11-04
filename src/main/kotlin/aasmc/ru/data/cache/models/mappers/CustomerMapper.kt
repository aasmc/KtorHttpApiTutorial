package aasmc.ru.data.cache.models.mappers

import aasmc.ru.data.Mapper
import aasmc.ru.data.cache.models.CachedCustomer
import aasmc.ru.domain.model.Customer

class CustomerMapper: Mapper<CachedCustomer, Customer> {
    override fun mapToDomain(entity: CachedCustomer): Customer = Customer(
        id = entity.id,
        firstName = entity.firstName,
        lastName = entity.lastName,
        email = entity.email
    )

    override fun mapToEntity(domain: Customer): CachedCustomer = CachedCustomer(
        id = domain.id,
        firstName = domain.firstName,
        lastName = domain.lastName,
        email = domain.email
    )
}
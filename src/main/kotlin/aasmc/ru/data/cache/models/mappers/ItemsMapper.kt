package aasmc.ru.data.cache.models.mappers

import aasmc.ru.data.Mapper
import aasmc.ru.data.cache.models.CachedItem
import aasmc.ru.domain.model.OrderItem

class ItemsMapper: Mapper<CachedItem, OrderItem> {
    override fun mapToDomain(entity: CachedItem): OrderItem = OrderItem(
        item = entity.item,
        amount = entity.amount,
        price = entity.price
    )

    override fun mapToEntity(domain: OrderItem): CachedItem = CachedItem(
        item = domain.item,
        amount = domain.amount,
        price = domain.price
    )
}
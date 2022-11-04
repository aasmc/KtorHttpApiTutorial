package aasmc.ru.data.cache.models.mappers

import aasmc.ru.data.Mapper
import aasmc.ru.data.cache.models.CachedOrder
import aasmc.ru.domain.model.Order

class OrdersMapper(
    private val itemsMapper: ItemsMapper = ItemsMapper()
): Mapper<CachedOrder, Order> {

    override fun mapToDomain(entity: CachedOrder): Order = Order(
        number = entity.number,
        contents = entity.items.map(itemsMapper::mapToDomain)
    )

    override fun mapToEntity(domain: Order): CachedOrder = CachedOrder(
        number = domain.number,
        items = domain.contents.map(itemsMapper::mapToEntity)
    ).also {
        it.items.forEach { item -> item.order = it }
    }
}
package aasmc.ru.playground.applications.appmodel.dao

import aasmc.ru.playground.applications.appmodel.model.Item
import aasmc.ru.playground.applications.appmodel.model.ItemBidSummary

interface ItemDao : GenericDAO<Item, Long> {
    fun findAll(withBids: Boolean): List<Item>

    fun findByName(name: String, fuzzy: Boolean): List<Item>

    fun findItemBidSummaries(): List<ItemBidSummary>
}
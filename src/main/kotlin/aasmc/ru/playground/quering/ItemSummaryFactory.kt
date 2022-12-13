package aasmc.ru.playground.quering

import java.time.LocalDate

object ItemSummaryFactory {
    fun newItemSummary(itemId: Long, name: String, auctionEnd: LocalDate): ItemSummary {
        return ItemSummary(itemId, name, auctionEnd)
    }
}
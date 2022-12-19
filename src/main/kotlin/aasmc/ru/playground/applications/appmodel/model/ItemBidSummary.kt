package aasmc.ru.playground.applications.appmodel.model

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class ItemBidSummary(
    val itemId: Long,
    val name: String,
    val auctionEnd: Date,
    val highestBid: BigDecimal
): Serializable
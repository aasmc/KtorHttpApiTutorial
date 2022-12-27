package aasmc.ru.playground.applications.appmodel.model

import jakarta.persistence.*
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.GenericGenerator
import java.io.Serializable
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hilo")
    @GenericGenerator(
        name = "hilo",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = [
            org.hibernate.annotations.Parameter(name = "sequence_name", value = "sequence"),
            org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
            org.hibernate.annotations.Parameter(name = "increment_size", value = "3"),
            org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        ]
    )
    var id: Long? = null,
    @NotNull
    @Size(
        min = 2,
        max = 255,
        message = "Name is requires, min 2, max 255 characters."
    )
    var name: String = "",

    @NotNull
    @Size(
        min = 10,
        max = 4000,
        message = "Description is required, minimum 10, maximum 4000 characters."
    )
    var description: String = "",
    @NotNull(message = "Auction end must be a future date and time.")
    @Future(message = "Auction end must be a future date and time.")
    var auctionEnd: Date? = null

) : Serializable {
    @NotNull
    @Version
    var version: Long = 0

    @OneToMany(mappedBy = "item")
    var bids: MutableSet<Bid> = hashSetOf()

    override fun toString(): String {
        return "ITEM ID: $id NAME: $name"
    }

    fun isValidBid(newBid: Bid?): Boolean {
        val highestBid = getHighestBid()
        if (newBid == null) return false
        if (newBid.amount.compareTo(BigDecimal.ZERO) != 1) {
            return false
        }
        if (highestBid == null) return true
        if (newBid.amount.compareTo(highestBid.amount) == 1) return true
        return false
    }

    fun getHighestBid(): Bid? {
        return if (bids.size > 0) {
            getBidsHighestFirst()[0]
        } else {
            null
        }
    }

    fun getBidsHighestFirst(): List<Bid> {
        val list = ArrayList(bids)
        list.sort()
        return list
    }

    fun isValidBid(newBid: Bid, currentHighestBid: Bid, currentLowestBid: Bid): Boolean {
        throw UnsupportedOperationException("Not implemented, example for a more flexible design.")
    }
}





















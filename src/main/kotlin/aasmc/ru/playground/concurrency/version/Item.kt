package aasmc.ru.playground.concurrency.version

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.math.BigDecimal

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,

    @NotNull
    var name: String = "",
    var buyNowPrice: BigDecimal = BigDecimal.ZERO,
    @ManyToOne(fetch = FetchType.LAZY)
    var category: Category? = null
) {
    /**
     * Hibernate automatically changes the version value: it increments the version number
     * whenever an Item instance has been found dirty during flushing of the persistence
     * cotext.
     */
    @Version
    protected var version: Long = 0

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "IMAGE",
        joinColumns = [JoinColumn(name = "ITEM_ID")]
    )
    @Column(name = "FILENAME")
    var images: MutableSet<String> = hashSetOf()

    // ...

    fun getCurrentVersion() = version
}
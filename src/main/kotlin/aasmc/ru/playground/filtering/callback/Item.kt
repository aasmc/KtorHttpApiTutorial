package aasmc.ru.playground.filtering.callback

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
/**
 * If several listeners define callback methods for the same event,
 * Hibernate invokes the listeners in the declared order.
 */
@EntityListeners(
    PersistEntityListener::class
)
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var name: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_ID", nullable = false)
    var seller: User = User()
) {

}
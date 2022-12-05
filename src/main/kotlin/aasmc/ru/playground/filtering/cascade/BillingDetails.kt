package aasmc.ru.playground.filtering.cascade

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class BillingDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @NotNull
    var owner: String = ""
)

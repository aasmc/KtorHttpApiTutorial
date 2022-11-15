package aasmc.ru.playground.inheritance.tableperclass

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

/**
 * With this strategy, when there's a polymorphic query like:
 * SELECT bd FROM BillingDetails bd
 *
 * Hibernate creates a subselect and uses UNION.
 *
 * This strategy allows polymorphic associations.
 * E.g. we could have an association mapping from User to BillingDetails.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class BillingDetails constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull
    var owner: String = ""

    constructor(owner: String):this() {
        this.owner = owner
    }
}
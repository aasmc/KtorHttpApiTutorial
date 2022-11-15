package aasmc.ru.playground.inheritance.singletable

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

/**
 * Shared properties will not be null, whereas all the properties
 * in subclasses will be nullable in the database, which implies
 * data integrity problems.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "BD_TYPE")
abstract class BillingDetails constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @NotNull // Ignored by Hibernate for schema generation
    @Column(nullable = false)
    var owner: String = ""

    constructor(owner: String): this() {
        this.owner = owner
    }

}
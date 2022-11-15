package aasmc.ru.playground.inheritance.mixed

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

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
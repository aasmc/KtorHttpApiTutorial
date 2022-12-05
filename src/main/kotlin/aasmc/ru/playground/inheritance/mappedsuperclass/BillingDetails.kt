package aasmc.ru.playground.inheritance.mappedsuperclass

import jakarta.persistence.MappedSuperclass
import jakarta.validation.constraints.NotNull

/**
 * Problems with implicit inheritance:
 * - doesn't support polymorphic associations very well: if subclasses are mapped to different
 *   tables, a polymorphic association to their superclass (abstract class BillingDetails) can't
 *   be represented as a simple foreign key relationship.
 * - Polymorphic queries that return instances of all classes that match the interface of the queried
 *   class are also problematic: Hibernate must execute a query against the superclass as several
 *   SQL SELECTs, one for each concrete subclass, e.g. the JPA SELECT bd FROM BillingDetails bd
 *   requires two SQL statements: for CreditCard and for BankAccount tables.
 * - schema evolution is complex since several different columns of different tables share exactly
 *   the same semantics.
 */
@MappedSuperclass
abstract class BillingDetails constructor() {
    @NotNull
    var owner: String = ""

    constructor(owner: String):this() {
        this.owner = owner
    }
}
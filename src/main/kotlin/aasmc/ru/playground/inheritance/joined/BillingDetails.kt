package aasmc.ru.playground.inheritance.joined

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

/**
 * In this strategy the primary key columns of subclasses have a foreign
 * key constraint referencing the primary key of the superclass.
 *
 * Hibernate relies on an SQL outer join for queries like:
 * select bd from BillingDetails bd:
 *
 *  select
 *     BD.ID, BD.OWNER,
 *     CC.EXPMONTH, CC.EXPYEAR, CC.CARDNUMBER,
 *     BA.ACCOUNT, BA.BANKNAME, BA.SWIFT,
 *     case
 *          when CC.CREDITCARD_ID is not null then 1
 *          when BA.ID is not null then 2
 *          when BD.ID is not null then 0
 *     end
 *  from BILLINGDETAILS BD
 *       left outer join CREDITCARD CC on BD.ID = cc.CREDITCARD_ID
 *       left outer join BANKACCOUNT BA on BD.ID = BA.ID
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
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

    fun pay(amount: Int) {
        println("Paying amount: $amount")
    }

}
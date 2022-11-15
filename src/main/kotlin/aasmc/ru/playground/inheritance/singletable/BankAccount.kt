package aasmc.ru.playground.inheritance.singletable

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.validation.constraints.NotNull

@Entity
@DiscriminatorValue("BA")
class BankAccount: BillingDetails {
    constructor(): super()

    @NotNull
    var account: String = ""

    @NotNull
    var bankName: String = ""

    @NotNull
    var swift: String = ""

    constructor(
        owner: String,
        account: String,
        bankname: String,
        swift: String
    ) : super(owner) {
        this.account = account
        this.bankName = bankName
        this.swift = swift
    }

}
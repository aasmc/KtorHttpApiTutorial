package aasmc.ru.playground.filtering.cascade

import jakarta.persistence.Entity
import jakarta.validation.constraints.NotNull

@Entity
class BankAccount: BillingDetails {
    constructor(): super()
    @NotNull
    var account: String = ""
    @NotNull
    var bankname: String = ""
    @NotNull
    var swift: String = ""
    constructor(
        account: String,
        bankname: String,
        swift: String,
        owner: String
    ): super(owner = owner) {
        this.account = account
        this.bankname = bankname
        this.swift = swift
    }
}
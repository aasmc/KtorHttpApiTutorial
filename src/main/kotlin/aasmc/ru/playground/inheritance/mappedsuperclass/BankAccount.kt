package aasmc.ru.playground.inheritance.mappedsuperclass

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull

@Entity
class BankAccount : BillingDetails {
    constructor() : super()

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

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

    override fun toString(): String {
        return "BankAccount: [id=$id, account=$account, bankName=$bankName, swift=:$swift]"
    }

}
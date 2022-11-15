package aasmc.ru.cache.hibernateproviders

import aasmc.ru.data.cache.hibernateproviders.interfaces.EntityProvider
import aasmc.ru.playground.inheritance.joined.BankAccount
import aasmc.ru.playground.inheritance.joined.BillingDetails
import aasmc.ru.playground.inheritance.joined.CreditCard

class InheritanceEntityProviderManyToOne : EntityProvider {
    override fun provideEntities(): Array<Class<*>> {
        return arrayOf(
            BillingDetails::class.java,
            aasmc.ru.playground.inheritance.associations.manytoone.User::class.java,
            CreditCard::class.java,
            BankAccount::class.java
        )
    }
}
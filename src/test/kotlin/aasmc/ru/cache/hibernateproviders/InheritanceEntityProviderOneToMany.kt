package aasmc.ru.cache.hibernateproviders

import aasmc.ru.data.cache.hibernateproviders.interfaces.EntityProvider
import aasmc.ru.playground.inheritance.associations.onetomany.BankAccount
import aasmc.ru.playground.inheritance.associations.onetomany.BillingDetails
import aasmc.ru.playground.inheritance.associations.onetomany.CreditCard
import aasmc.ru.playground.inheritance.associations.onetomany.User

class InheritanceEntityProviderOneToMany : EntityProvider {
    override fun provideEntities(): Array<Class<*>> {
        return arrayOf(
            User::class.java,
            BillingDetails::class.java,
            CreditCard::class.java,
            BankAccount::class.java
        )
    }
}
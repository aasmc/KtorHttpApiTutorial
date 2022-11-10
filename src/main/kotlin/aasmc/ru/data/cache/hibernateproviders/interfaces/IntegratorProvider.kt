package aasmc.ru.data.cache.hibernateproviders.interfaces

import org.hibernate.integrator.spi.Integrator

interface IntegratorProvider {
    fun provideIntegrator(): Integrator?
}
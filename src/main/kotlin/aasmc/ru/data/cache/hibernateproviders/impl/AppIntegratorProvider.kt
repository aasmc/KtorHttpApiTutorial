package aasmc.ru.data.cache.hibernateproviders.impl

import aasmc.ru.data.cache.hibernateproviders.interfaces.IntegratorProvider
import org.hibernate.integrator.spi.Integrator

class AppIntegratorProvider: IntegratorProvider {
    override fun provideIntegrator(): Integrator? {
        return null
    }
}
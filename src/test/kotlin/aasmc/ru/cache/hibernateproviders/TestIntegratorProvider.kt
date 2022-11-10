package aasmc.ru.cache.hibernateproviders

import aasmc.ru.data.cache.hibernateproviders.interfaces.IntegratorProvider
import org.hibernate.integrator.spi.Integrator

class TestIntegratorProvider: IntegratorProvider {
    override fun provideIntegrator(): Integrator? {
        return null
    }
}
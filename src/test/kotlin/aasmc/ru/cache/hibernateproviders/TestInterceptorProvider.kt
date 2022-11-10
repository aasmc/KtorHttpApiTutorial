package aasmc.ru.cache.hibernateproviders

import aasmc.ru.data.cache.hibernateproviders.interfaces.InterceptorProvider
import org.hibernate.Interceptor

class TestInterceptorProvider: InterceptorProvider {
    override fun provideInterceptor(): Interceptor? {
        return null
    }
}
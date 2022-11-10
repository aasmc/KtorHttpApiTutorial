package aasmc.ru.data.cache.hibernateproviders.impl

import aasmc.ru.data.cache.hibernateproviders.interfaces.InterceptorProvider
import org.hibernate.Interceptor

class AppInterceptorProvider : InterceptorProvider {
    override fun provideInterceptor(): Interceptor? {
        return null
    }
}
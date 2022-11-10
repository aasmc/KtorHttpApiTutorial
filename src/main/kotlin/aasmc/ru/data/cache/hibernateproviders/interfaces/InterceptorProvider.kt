package aasmc.ru.data.cache.hibernateproviders.interfaces

import org.hibernate.Interceptor

interface InterceptorProvider {
    fun provideInterceptor(): Interceptor?
}
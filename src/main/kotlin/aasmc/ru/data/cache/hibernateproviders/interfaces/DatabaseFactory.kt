package aasmc.ru.data.cache.hibernateproviders.interfaces

import jakarta.persistence.EntityManagerFactory
import org.hibernate.SessionFactory

interface DatabaseFactory {
    fun createSessionFactory(): SessionFactory
    fun createEntityManagerFactory(): EntityManagerFactory
}
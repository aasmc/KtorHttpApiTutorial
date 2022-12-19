package aasmc.ru.playground.applications.appmodel.dao

import jakarta.persistence.LockModeType
import java.io.Serializable

/**
 * An interface shared by all business data access objects.
 * <p>
 * All CRUD (create, read, update, delete) basic data access operations are
 * isolated in this interface and shared across all DAO implementations.
 * The current design is for a state-management oriented persistence layer
 * (for example, there is no UPDATE statement function) which provides
 * automatic transactional dirty checking of business objects in persistent
 * state.
 */
interface GenericDAO<T, ID: Serializable> {
    fun findById(id: ID): T

    fun findById(id: ID, lockModeType: LockModeType): T

    fun findReferenceById(id: ID): T

    fun findAll(): MutableList<T>

    fun getCount(): Long

    fun makePersistent(entity: T): T

    fun makeTransient(entity: T)

    fun checkVersion(entity: T, forceUpdate: Boolean)
}
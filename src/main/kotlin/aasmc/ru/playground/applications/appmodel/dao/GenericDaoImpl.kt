package aasmc.ru.playground.applications.appmodel.dao

import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import jakarta.persistence.PersistenceContext
import java.io.Serializable

abstract class GenericDaoImpl<T, ID : Serializable>(
    protected val entityClass: Class<T>
) : GenericDAO<T, ID> {

    @PersistenceContext
    protected var _em: EntityManager? = null

    protected var em: EntityManager = _em!!

    override fun findById(id: ID): T {
        return findById(id, LockModeType.NONE)
    }

    override fun findById(id: ID, lockModeType: LockModeType): T {
        return em.find(entityClass, id, lockModeType)
    }

    override fun findReferenceById(id: ID): T {
        return em.getReference(entityClass, id)
    }

    override fun findAll(): MutableList<T> {
        val c = em.criteriaBuilder.createQuery(entityClass)
        c.select(c.from(entityClass))
        return em.createQuery(c).resultList
    }

    override fun getCount(): Long {
        val c = em.criteriaBuilder.createQuery(Long::class.java)
        c.select(em.criteriaBuilder.count(c.from(entityClass)))
        return em.createQuery(c).singleResult
    }

    override fun makePersistent(entity: T): T {
        // merge() handles transient AND detached instances
        return em.merge(entity)
    }

    override fun makeTransient(entity: T) {
        em.remove(entity)
    }

    override fun checkVersion(entity: T, forceUpdate: Boolean) {
        em.lock(
            entity,
            if (forceUpdate) LockModeType.OPTIMISTIC_FORCE_INCREMENT
            else LockModeType.OPTIMISTIC
        )
    }

}

























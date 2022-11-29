package aasmc.ru.playground.concurrency

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.concurrency.version.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NonTransactional : Versioning() {

    private val ORIGINAL_NAME = "Original Name"
    private val NEW_NAME = "New Name"

    @Test
    fun autoCommit() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item(name = ORIGINAL_NAME)
            persist(item)
            item.id
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data

        /*
           No transaction is active when we create the <code>EntityManager</code>. The
           persistence context is now in a special <em>unsynchronized</em> mode, Hibernate
           will not flush automatically at any time.
         */
        val em = entityManagerFactory.createEntityManager()
        val item = em.find(Item::class.java, id)
        item.name = NEW_NAME
        /*
           Usually Hibernate would flush the persistence context when you execute a
           <code>Query</code>. However, because the context is <em>unsynchronized</em>,
           flushing will not occur and the query will return the old, original database
           value. Queries with scalar results are not repeatable, you'll see whatever
           values are present in the database and given to Hibernate in the
           <code>ResultSet</code>. Note that this isn't a repeatable read either if
           you are in <em>synchronized</em> mode.
         */
        assertEquals(
            ORIGINAL_NAME,
            em.createQuery("select i.name from Item i where i.id = :id", String::class.java)
                .setParameter("id", id)
                .singleResult
        )

        /*
           Retrieving a managed entity instance involves a lookup, during JDBC
           result set marshaling, in the current persistence context. The
           already loaded <code>Item</code> instance with the changed name will
           be returned from the persistence context, values from the database
           will be ignored. This is a repeatable read of an entity instance,
           even without a system transaction.
         */
        assertEquals(
            NEW_NAME,
            em.createQuery("select i from Item i where i.id = :id", Item::class.java)
                .setParameter("id", id)
                .singleResult.name
        )

        /*
            If you try to flush the persistence context manually, to store the new
            <code>Item#name</code>, Hibernate will throw a
            <code>javax.persistence.TransactionRequiredException</code>. You are
            prevented from executing an <code>UPDATE</code> statement in
            <em>unsynchronized</em> mode, as you wouldn't be able to roll back the change.
         */
        // em.flush();

        /*
           You can roll back the change you made with the <code>refresh()</code>
           method, it loads the current <code>Item</code> state from the database
           and overwrites the change you have made in memory.
         */
        em.refresh(item)
        assertEquals(item.name, ORIGINAL_NAME)

        val newItem = Item(name = "New Item")
        /*
            You can call <code>persist()</code> to save a transient entity instance with an
            unsynchronized persistence context. Hibernate will only fetch a new identifier
            value, typically by calling a database sequence, and assign it to the instance.
            The instance is now in persistent state in the context but the SQL
            <code>INSERT</code> hasn't happened. Note that this is only possible with
            <em>pre-insert</em> identifier generators; see <a href="#GeneratorStrategies"/>.
         */
        em.persist(newItem)
        assertTrue(newItem.id != 0L)
        /*
          When you are ready to store the changes, join the persistence context with
          a transaction. Synchronization and flushing will occur as usual, when the
          transaction commits. Hibernate writes all queued operations to the database.
      */
        entityManagerFactory.withEntityManager {
            if (!em.isJoinedToTransaction) {
                em.joinTransaction()
            }
        }
    }
}


















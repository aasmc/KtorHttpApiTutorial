package aasmc.ru.playground.transitions

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.model.Item
import jakarta.persistence.FlushModeType
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.hibernate.Hibernate
import org.hibernate.Session
import org.junit.jupiter.api.Test
import java.sql.SQLException
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SimpleTransitions : AbstractTest() {

    @Test
    fun retrievePersistentReference() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item().apply {
                name = "Some Item"
            }

            persist(item)
            item.getId()
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data ?: throw IllegalStateException()

        val itemRes = entityManagerFactory.withEntityManager {
            /*
              If the persistence context already contains an <code>Item</code> with the given identifier, that
              <code>Item</code> instance is returned by <code>getReference()</code> without hitting the database.
              Furthermore, if <em>no</em> persistent instance with that identifier is currently managed, a hollow
              placeholder will be produced by Hibernate, a proxy. This means <code>getReference()</code> will not
              access the database, and it doesn't return <code>null</code>, unlike <code>find()</code>.
            */
            val item = getReference(Item::class.java, id)
            val util = entityManagerFactory.persistenceUnitUtil
            assertFalse(util.isLoaded(item))

            /*
             As soon as you call any method such as <code>Item#getName()</code> on the proxy, a
             <code>SELECT</code> is executed to fully initialize the placeholder. The exception to this rule is
             a method that is a mapped database identifier getter method, such as <code>getId()</code>. A proxy
             might look like the real thing but it is only a placeholder carrying the identifier value of the
             entity instance it represents. If the database record doesn't exist anymore when the proxy is
             initialized, an <code>EntityNotFoundException</code> will be thrown.
           */
            // assertEquals(item.getName(), "Some Item");
            /*
               Hibernate has a convenient static <code>initialize()</code> method, loading the proxy's data.
             */
            Hibernate.initialize(item)
            item

        }
        assertTrue(itemRes is Result.Success)

        /*
          After the persistence context is closed, <code>item</code> is in detached state. If you do
          not initialize the proxy while the persistence context is still open, you get a
          <code>LazyInitializationException</code> if you access the proxy. You can't load
          data on-demand once the persistence context is closed. The solution is simple: Load the
          data before you close the persistence context.
        */
        val item = itemRes.data
        assertEquals("Some Item", item.name)
    }

    @Test
    fun makeTransient() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val someItem = Item().apply {
                name = "Some Item"
            }
            persist(someItem)
            someItem.getId()
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data ?: throw IllegalStateException()
        val itemRes = entityManagerFactory.withEntityManager {

            /*
               If you call <code>find()</code>, Hibernate will execute a <code>SELECT</code> to
               load the <code>Item</code>. If you call <code>getReference()</code>, Hibernate
               will attempt to avoid the <code>SELECT</code> and return a proxy.
             */
            val item = find(Item::class.java, id)

            /*
              Calling <code>remove()</code> will queue the entity instance for deletion when
              the unit of work completes, it is now in <em>removed</em> state. If <code>remove()</code>
              is called on a proxy, Hibernate will execute a <code>SELECT</code> to load the data.
              An entity instance has to be fully initialized during life cycle transitions. You may
              have life cycle callback methods or an entity listener enabled
              (see <a href="#EventListenersInterceptors"/>), and the instance must pass through these
              interceptors to complete its full life cycle.
            */
            remove(item)

            /*
              You can make the removed instance persistent again, cancelling the deletion.
            */
            // em.persist(item);
            item
        }

        assertTrue(itemRes is Result.Success)

        // removed from the DB
        val item = itemRes.data
        val removedItemRes = entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)
            assertNull(item)
            item
        }

        assertTrue(removedItemRes is Result.Success)
        assertNull(removedItemRes.data)
    }


    @Test
    fun refreshTest() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item().apply {
                name = "Some Item"
            }
            persist(item)
            item.getId()
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data ?: throw IllegalStateException()

        entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)
            item.name = "Some Name"

           async {
                val session = unwrap(Session::class.java)
                session.doWork() { connection ->
                    val ps =
                        connection.prepareStatement("update ITEM set item_name = ? where ID = ?")
                    ps.setString(1, "Concurrent Update Name")
                    ps.setLong(2, id)

                    /* Alternative: you get an EntityNotFoundException on refresh
                       PreparedStatement ps = con.prepareStatement("delete from ITEM where ID = ?");
                       ps.setLong(1, ITEM_ID);
                    */

                    if (ps.executeUpdate() != 1) {
                        throw SQLException("ITEM row was not updated!")
                    }
                }
            }.await()

            val oldName = item.name
            refresh(item)
            assertNotEquals(oldName, item.name)
            assertEquals("Concurrent Update Name", item.name)
        }
    }

    @Test
    fun flushModeType() = runTest {
        val idRes = entityManagerFactory.withEntityManager {
            val item = Item().apply {
                name = "Original Name"
            }

            persist(item)
            item.getId()
        }
        assertTrue(idRes is Result.Success)
        val id = idRes.data

        val res = entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, id)
            item.name = "New Name"

            // Disable flushing before queries
            flushMode = FlushModeType.COMMIT

            assertEquals(
                createQuery("select i.name from Item i where i.id = :id", String::class.java)
                    .setParameter("id", id).singleResult,
                "Original Name"
            )
        }

        assertTrue(res is Result.Success)
    }

}

































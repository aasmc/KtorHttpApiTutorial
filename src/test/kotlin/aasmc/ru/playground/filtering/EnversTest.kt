package aasmc.ru.playground.filtering

import aasmc.ru.data.cache.withEntityManager
import aasmc.ru.domain.model.Result
import aasmc.ru.playground.AbstractTest
import aasmc.ru.playground.filtering.envers.Bid
import aasmc.ru.playground.filtering.envers.Item
import aasmc.ru.playground.filtering.envers.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.DefaultRevisionEntity
import org.hibernate.envers.RevisionType
import org.hibernate.envers.query.AuditEntity
import org.hibernate.envers.query.criteria.MatchMode
import org.junit.jupiter.api.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EnversTest: AbstractTest(
    entityProvider = {
        arrayOf(
            Bid::class.java,
            User::class.java,
            Item::class.java
        )
    }
) {

    @Test
    fun auditLogging() = runTest {
        val idsRes = entityManagerFactory.withEntityManager {
            val user = User(username = "johndoe")
            persist(user)

            val item = Item(name = "Foo", seller = user)
            persist(item)

            listOf(user.id, item.id)
        }

        assertTrue(idsRes is Result.Success)
        val (userId, itemId) = idsRes.data

        val createTimeStamp = Date()

        val update = entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, itemId)
            item.name = "Bar"
            item.seller?.username = "doejohn"
        }
        assertTrue(update is Result.Success)

        val updateTimeStamp = Date()
        val delete = entityManagerFactory.withEntityManager {
            val item = find(Item::class.java, itemId)
            remove(item)
        }
        assertTrue(delete is Result.Success)

        val deleteTimeStamp = Date()

        val auditRes = entityManagerFactory.withEntityManager {
                /*
                  The main Envers API is the <code>AuditReader</code>, it can be accessed with
                  an <code>EntityManager</code>.
                */
            val auditReader = AuditReaderFactory.get(this)
            /*
               Given a timestamp, you can find the revision number of a change set, made
               before or on that timestamp. Revision numbers are sequentially incremented, i.e.
               the higher the number the more recent version of an entity instance.
             */
            val revisionCreate = auditReader.getRevisionNumberForDate(createTimeStamp)
            val revisionUpdate = auditReader.getRevisionNumberForDate(updateTimeStamp)
            val revisionDelete = auditReader.getRevisionNumberForDate(deleteTimeStamp)

            /*
               If you don't have a timestamp, you can get all revision numbers in which a
               particular audited entity instance was involved. This operation finds all
               change sets where the given <code>Item</code> was created, modified, or
               deleted. In our example, we created, modified, and then deleted the
               <code>Item</code>. Hence, we have three revisions.
             */
            val itemRevisions = auditReader.getRevisions(Item::class.java, itemId)
            assertEquals(3, itemRevisions.size)

            for(itemRevision in itemRevisions) {
                /*
                  If you have a revision number, you can get the timestamp when Envers
                  logged the change set.
                */
                val itemRevisionTimeStamp = auditReader.getRevisionDate(itemRevision)
                // ...
            }
            /*
              We created and modified the <code>User</code>, so there are two revisions.
            */
            val userRevisions = auditReader.getRevisions(User::class.java, userId)
            assertEquals(2, userRevisions.size)

            /*
              If you don't know modification timestamps or revision numbers, you can write
              a query with <code>forRevisionsOfEntity()</code> to obtain all audit trail
              details of a particular entity.
            */
            val query = auditReader.createQuery()
                .forRevisionsOfEntity(Item::class.java, false, false)

            val result = query.resultList as MutableList<Array<Any>>
            for(tuple in result) {
                val item = tuple[0] as? Item
                val revision = tuple[1] as DefaultRevisionEntity
                val revisionType = tuple[2] as RevisionType
                  /*
                    The revision type indicates why Envers created the revision, because
                    the entity instance was inserted, modified, or deleted in the database.
                  */
                if (revision.id == 1) {
                    assertEquals(RevisionType.ADD, revisionType)
                    assertEquals("Foo", item?.name)
                } else if (revision.id == 2) {
                    assertEquals(RevisionType.MOD, revisionType)
                    assertEquals("Bar", item?.name)
                } else if (revision.id == 3) {
                    assertEquals(RevisionType.DEL, revisionType)
                    assertNull(item)
                }
            }
              /*
                The <code>find()</code> method returns an audited entity instance version,
                given a revision. This operation loads the <code>Item</code> as it was after
                creation.
              */
            val item = auditReader.find(Item::class.java, itemId, revisionCreate)
            assertEquals("Foo", item.name)
            assertEquals("johndoe", item.seller.username)

               /*
                 This operation loads the <code>Item</code> after it was updated. Note how
                 the modified <code>seller</code> of this change set was also retrieved
                 automatically.
               */
            val modifiedItem = auditReader.find(Item::class.java, itemId, revisionUpdate)
            assertEquals("Bar", modifiedItem.name)
            assertEquals("doejohn", modifiedItem.seller.username)

            /*
               In this revision, the <code>Item</code> was deleted, so <code>find()</code>
               returns <code>null</code>.
             */
            val deletedItem = auditReader.find(Item::class.java, itemId, revisionDelete)
            assertNull(deletedItem)

              /*
                However, the example did not modify the <code>User</code> in this revision,
                so Envers returns its closest historical revision.
              */
            val user = auditReader.find(User::class.java, userId, revisionDelete)
            assertEquals("doejohn", user.username)

            /*
             This query returns <code>Item</code> instances restricted to a
             particular revision and change set.
           */
            val restrictedQuery = auditReader.createQuery()
                .forEntitiesAtRevision(Item::class.java, revisionUpdate)
            /*
                You can add further restrictions to the query; here the <code>Item#name</code>
                must start with "Ba".
             */
            restrictedQuery.add(
                AuditEntity.property("name").like("Ba", MatchMode.START)
            )

            /*
              Restrictions can include entity associations, for example, we are looking for
              the revision of an <code>Item</code> sold by a particular <code>User</code>.
            */
            restrictedQuery.add(
                AuditEntity.relatedId("seller").eq(userId)
            )

            // You can order query results
            restrictedQuery.addOrder(
                AuditEntity.property("name").desc()
            )

            // you can paginate through large results
            restrictedQuery.setFirstResult(0)
            restrictedQuery.setMaxResults(10)

            assertEquals(1, restrictedQuery.resultList.size)
            val itemRes = restrictedQuery.resultList[0] as Item
            assertEquals("doejohn", itemRes.seller.username)

            val updateQuery = auditReader.createQuery()
                .forEntitiesAtRevision(Item::class.java, revisionUpdate)

            updateQuery.addProjection(
                AuditEntity.property("name")
            )
            assertEquals(1, updateQuery.resultList.size)
            val nameRes = updateQuery.singleResult as String
            assertEquals("Bar", nameRes)
        }

        assertTrue(auditRes is Result.Success)
    }

}






















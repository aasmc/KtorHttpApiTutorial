package aasmc.ru.playground.collections.sortedmapofstrings

import jakarta.persistence.*
import java.util.*
import kotlin.Comparator

/**
 * Sorting occurs in-memory with the help of TreeMap.
 */
@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0


    @ElementCollection
    @CollectionTable(name = "IMAGE")
    @MapKeyColumn(name = "FILENAME") // maps the key
    @Column(name = "IMAGENAME") // maps the value
    @org.hibernate.annotations.SortComparator(ReverseStringComparator::class)
    var images: SortedMap<String, String> = TreeMap()

    class ReverseStringComparator: Comparator<String> {
        override fun compare(o1: String, o2: String): Int {
            return o2.compareTo(o1)
        }
    }
}
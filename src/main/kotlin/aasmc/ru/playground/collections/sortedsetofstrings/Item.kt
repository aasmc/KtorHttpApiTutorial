package aasmc.ru.playground.collections.sortedsetofstrings

import jakarta.persistence.*
import java.util.*

@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @ElementCollection
    @CollectionTable(name = "IMAGE")
    @Column(name = "FILENAME") // defaults to IMAGES
    @org.hibernate.annotations.SortNatural
    var images: SortedSet<String> = TreeSet()
}
package aasmc.ru.playground.collections.setofstringsorderby

import jakarta.persistence.*
import kotlin.collections.LinkedHashSet

@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @ElementCollection
    @CollectionTable(name = "IMAGE")
    @Column(name = "FILENAME") // defaults to IMAGES
//    @jakarta.persistence.OrderBy // Only one possible order: "FILENAME asc" because
//    the value is of BasicType String
    @org.hibernate.annotations.OrderBy(clause = "FILENAME desc")
    var images: MutableSet<String> = LinkedHashSet()
}
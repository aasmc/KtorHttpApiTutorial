package aasmc.ru.playground.collections.bagofstrings

import jakarta.persistence.*
import org.hibernate.type.descriptor.java.LongJavaType

@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @ElementCollection
    @CollectionTable(name = "IMAGE")
    @Column(name = "FILENAME") // defaults to IMAGES
    @org.hibernate.annotations.CollectionIdJavaType(
        LongJavaType::class
    )
    @org.hibernate.annotations.CollectionId(
        column = Column(name = "IMAGE_ID"),
        generator = "sequence"
    )
    var images: MutableCollection<String> = arrayListOf() // No BagImpl in JDK!
}
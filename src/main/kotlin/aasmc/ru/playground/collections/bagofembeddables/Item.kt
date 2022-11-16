package aasmc.ru.playground.collections.bagofembeddables

import jakarta.persistence.*
import org.hibernate.type.descriptor.java.LongJavaType


@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    /**
     * With this type of mapping, Hibernate creates a single surrogate primary
     * key IMAGE_ID. This allows duplicates and null values for some of the
     * properties if Image
     */
    @ElementCollection
    @CollectionTable(name = "IMAGE")
    @org.hibernate.annotations.CollectionIdJavaType(
        LongJavaType::class
    )
    @org.hibernate.annotations.CollectionId(
        column = Column(name = "IMAGE_ID"),
        generator = "sequence"
    )
    var images: MutableCollection<Image> = arrayListOf()
}
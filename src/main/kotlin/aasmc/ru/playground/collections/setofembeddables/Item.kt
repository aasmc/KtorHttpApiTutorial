package aasmc.ru.playground.collections.setofembeddables

import jakarta.persistence.*

@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @ElementCollection
    @CollectionTable(name = "IMAGE")
    @AttributeOverride(
        name = "filename",
        column = Column(name = "FNAME", nullable = false)
    )
    var images: MutableSet<Image> = hashSetOf()
}
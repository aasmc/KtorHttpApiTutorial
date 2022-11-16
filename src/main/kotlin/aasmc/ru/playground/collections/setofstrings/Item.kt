package aasmc.ru.playground.collections.setofstrings

import jakarta.persistence.*

@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @ElementCollection // required for a collection of value-typed elements
    // the IMAGE table has a composite primary key of both ITEM_ID and FILENAME columns
    @CollectionTable(
        name = "IMAGE", // Defaults to ITEM_IMAGES
        joinColumns = [JoinColumn(name = "ITEM_ID")] // default actually
    )
    @Column(name = "FILENAME") // defaults to IMAGES
    var images: MutableSet<String> = hashSetOf()
}
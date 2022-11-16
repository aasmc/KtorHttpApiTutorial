package aasmc.ru.playground.collections.listofstrings

import jakarta.persistence.*

/**
 * This type of mapping is not very efficient, since Hibernate
 * has to perform many UPDATE operations when inserting an item
 * in the middle of the collection or deleting an item from the
 * middle of the collection, because Hibernate needs to preserve
 * the order of the elements.
 */
@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    @ElementCollection
    @CollectionTable(name = "IMAGE")
    @OrderColumn // enables persistent order, Defaults to IMAGES_ORDER
    @Column(name = "FILENAME")
    var images: MutableList<String> = arrayListOf()
}
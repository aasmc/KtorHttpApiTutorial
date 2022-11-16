package aasmc.ru.playground.collections.mapofstringsembeddables

import jakarta.persistence.*

@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0

    /**
     * With this mapping, the PK is the IMAGE_ID and the Map Key Column - FILENAME
     */
    @ElementCollection
    @CollectionTable(name = "IMAGE")
    @MapKeyColumn(name = "FILENAME") // Optional, defaults to IMAGES_KEY
    var images: MutableMap<String, Image> = hashMapOf()
}
package aasmc.ru.playground.collections.mapofstringsorderby

import jakarta.persistence.*

@Entity
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0


    @ElementCollection
    @CollectionTable(name = "IMAGE")
//    @MapKeyEnumerated  // used if key is an Enum
//    @MapKeyTemporal(TemporalType.TIMESTAMP) // used if key is a temporal value
    @MapKeyColumn(name = "FILENAME") // maps the key
    @Column(name = "IMAGENAME") // maps the value
    @org.hibernate.annotations.OrderBy(clause = "FILENAME desc")
    var images: MutableMap<String, String> = LinkedHashMap()
}
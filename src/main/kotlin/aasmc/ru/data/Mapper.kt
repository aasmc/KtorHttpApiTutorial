package aasmc.ru.data

interface Mapper<Entity, Domain> {
    fun mapToDomain(entity: Entity): Domain
    fun mapToEntity(domain: Domain): Entity
}
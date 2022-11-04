package aasmc.ru.domain.model

data class User(
    val id: Long = 0,
    val username: String,
    val password: String,
    val salt: String
)

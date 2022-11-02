package aasmc.ru.security.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val expiresIn: Long
)

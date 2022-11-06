package aasmc.ru.routes

import aasmc.ru.domain.model.Result
import aasmc.ru.domain.model.User
import aasmc.ru.domain.repositories.SecurityRepository
import aasmc.ru.security.hashing.HashingService
import aasmc.ru.security.hashing.SaltedHash
import aasmc.ru.security.model.requests.AuthRequest
import aasmc.ru.security.model.responses.AuthResponse
import aasmc.ru.security.token.TokenClaim
import aasmc.ru.security.token.TokenConfig
import aasmc.ru.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.codec.digest.DigestUtils

fun Route.signUp(
    hashingService: HashingService,
    securityRepository: SecurityRepository
) {
    post("signup") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        when (val result = securityRepository.userAlreadyExists(request.username)) {
            is Result.Failure -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Something went wrong with the server while checking user ${request.username}"
                )
            }
            is Result.Success -> {
                val exists = result.data
                if (exists) {
                    call.respond(HttpStatusCode.Conflict, "User with the same username already exists in the database.")
                    return@post
                }
            }
        }

        if (!requestFieldsVerified(request)) {
            call.respond(
                HttpStatusCode.Conflict,
                "Incorrect username or password. Password must be at least 8 characters long"
            )
            return@post
        }
        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )

        when (securityRepository.insertUser(user)) {
            is Result.Failure -> {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            is Result.Success -> {
                call.respondText("User has been successfully saved to the DB.")
            }
        }
    }
}

private fun requestFieldsVerified(request: AuthRequest): Boolean {
    val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
    val isPasswordTooShort = request.password.length < 8
    return !(areFieldsBlank || isPasswordTooShort)
}

fun Route.signIn(
    securityRepository: SecurityRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        when (val userResult = securityRepository.getUserByUsername(request.username)) {
            is Result.Failure -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Something went wrong when signing in user with username: ${request.username}"
                )
            }
            is Result.Success -> {
                if (userResult.data == null) {
                    call.respond(HttpStatusCode.Conflict, "Incorrect username or password.")
                    return@post
                } else {
                    val user = userResult.data
                    val isValidPassword = hashingService.verify(
                        value = request.password,
                        saltedHash = SaltedHash(
                            hash = user.password,
                            salt = user.salt
                        )
                    )
                    if (!isValidPassword) {
                        println("Entered hash: ${DigestUtils.sha256Hex("${user.salt}${request.password}")}")
                        println("Hashed PW: ${user.password}")
                        call.respond(HttpStatusCode.Conflict, "Incorrect password.")
                        return@post
                    }
                    val token = tokenService.generate(
                        config = tokenConfig,
                        TokenClaim(
                            name = "userId",
                            value = user.id.toString()
                        )
                    )
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = AuthResponse(token = token, tokenConfig.expiresIn)
                    )
                }
            }
        }
    }
}




































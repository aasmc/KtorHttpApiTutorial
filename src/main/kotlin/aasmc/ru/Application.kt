package aasmc.ru

import aasmc.ru.database.DatabaseFactory
import io.ktor.server.application.*
import aasmc.ru.plugins.*
import aasmc.ru.security.hashing.SHA256HashingService
import aasmc.ru.security.token.JwtTokenService
import aasmc.ru.security.token.TokenConfig

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 24L * 60L * 60L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    val tokenService = JwtTokenService()

    DatabaseFactory.init(this.environment.config)
    configureMonitoring()
    configureSerialization()
    configureSecurity(config = tokenConfig)
    configureRouting(
        dao = DatabaseFactory.daoCache,
        hashingService = hashingService,
        securityDao = DatabaseFactory.securityDao,
        tokenService = tokenService,
        tokenConfig = tokenConfig
    )
}

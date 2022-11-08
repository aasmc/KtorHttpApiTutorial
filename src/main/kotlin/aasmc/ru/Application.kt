package aasmc.ru

import aasmc.ru.data.repositoriesimpl.CustomerRepositoryImpl
import aasmc.ru.data.repositoriesimpl.OrdersRepositoryImpl
import aasmc.ru.data.repositoriesimpl.SecurityRepositoryImpl
import aasmc.ru.plugins.configureMonitoring
import aasmc.ru.plugins.configureRouting
import aasmc.ru.plugins.configureSecurity
import aasmc.ru.plugins.configureSerialization
import aasmc.ru.security.hashing.SHA256HashingService
import aasmc.ru.security.token.JwtTokenService
import aasmc.ru.security.token.TokenConfig
import io.ktor.server.application.*

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

    configureMonitoring()
    configureSerialization()
    configureSecurity(config = tokenConfig)
    configureRouting(
        customersRepository = CustomerRepositoryImpl(config = environment.config),
        ordersRepository = OrdersRepositoryImpl(config = environment.config),
        hashingService = hashingService,
        securityRepository = SecurityRepositoryImpl(config = environment.config),
        tokenService = tokenService,
        tokenConfig = tokenConfig
    )
}

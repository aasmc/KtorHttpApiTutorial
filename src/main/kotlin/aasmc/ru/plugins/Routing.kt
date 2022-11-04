package aasmc.ru.plugins

import aasmc.ru.domain.repositories.CustomersRepository
import aasmc.ru.domain.repositories.OrdersRepository
import aasmc.ru.domain.repositories.SecurityRepository
import aasmc.ru.routes.*
import aasmc.ru.security.hashing.HashingService
import aasmc.ru.security.token.TokenConfig
import aasmc.ru.security.token.TokenService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureRouting(
    customersRepository: CustomersRepository,
    ordersRepository: OrdersRepository,
    hashingService: HashingService,
    securityRepository: SecurityRepository,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        authenticate("auth-jwt") {
            customerRouting(customersRepository)
            listOrderRoute(ordersRepository)
            getOrderRoute(ordersRepository)
            totalizeOrderRoute(ordersRepository)
            postOrderRoute(ordersRepository)
        }
        signUp(hashingService, securityRepository)
        signIn(
            securityRepository = securityRepository,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig
        )
    }
}

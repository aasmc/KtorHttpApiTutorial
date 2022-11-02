package aasmc.ru.plugins

import aasmc.ru.database.dao.DAOFacade
import aasmc.ru.routes.*
import aasmc.ru.security.database.dao.SecurityDAOFacade
import aasmc.ru.security.hashing.HashingService
import aasmc.ru.security.token.TokenConfig
import aasmc.ru.security.token.TokenService
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureRouting(
    dao: DAOFacade,
    hashingService: HashingService,
    securityDao: SecurityDAOFacade,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        authenticate("auth-jwt") {
            customerRouting(dao)
            listOrderRoute(dao)
            getOrderRoute(dao)
            totalizeOrderRoute(dao)
            postOrderRoute(dao)
        }
        signUp(hashingService, securityDao)
        signIn(
            securityDao = securityDao,
            hashingService = hashingService,
            tokenService = tokenService,
            tokenConfig = tokenConfig
        )
    }
}

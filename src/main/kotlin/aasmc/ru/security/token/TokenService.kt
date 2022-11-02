package aasmc.ru.security.token

interface TokenService {
    /**
     * Generates a token, given a [TokenConfig] and [TokenClaim]s.
     */
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String
}
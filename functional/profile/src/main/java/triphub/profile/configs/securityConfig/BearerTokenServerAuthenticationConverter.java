package triphub.profile.configs.securityConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import triphub.profile.exceptionHandling.exceptions.AuthException;

@RequiredArgsConstructor
public class BearerTokenServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final JwtHandler jwtHandler;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String token = exchange.getRequest().getCookies().getFirst("access_token") != null 
            ? exchange.getRequest().getCookies().getFirst("access_token").getValue() 
            : null;
        if (token == null) {
            return Mono.empty();
        }
        return jwtHandler.check(token)
            .flatMap(verificationResult -> {
                String tokenType = verificationResult.claims.get("type", String.class);
                if (!"ACCESS".equals(tokenType)) {
                    return Mono.error(new AuthException("Invalid token type", "INVALID_TOKEN_TYPE"));
                }
                return UserAuthenticationBearer.create(verificationResult);
            });
    }
}

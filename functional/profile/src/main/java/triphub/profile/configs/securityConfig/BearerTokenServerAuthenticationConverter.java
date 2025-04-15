package triphub.profile.configs.securityConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BearerTokenServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final JwtHandler jwtHandler;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String token = exchange.getRequest().getCookies().getFirst("jwt") != null 
            ? exchange.getRequest().getCookies().getFirst("jwt").getValue() 
            : null;
        if (token == null) {
            return Mono.empty();
        }
        return jwtHandler.check(token)
            .flatMap(verificationResult -> 
                UserAuthenticationBearer.create(verificationResult)
            );
    }
}

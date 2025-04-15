package triphub.gateway.configs.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // // 1. Публичные эндпоинты (без JWT)
                        // .pathMatchers(
                        //         "/api/auth/**",                  // Security Service
                        //         "/api/reports/by_arrivalto/{arrival_to}",       // GET /api/reports
                        //         "/api/reports/{id}"                             // GET /api/reports/{id}
                        // ).permitAll()

                        // // 2. Защищённые эндпоинты (требуют JWT)
                        // .anyExchange().authenticated()
                        .anyExchange().permitAll()
                )
                .build();
    }
}
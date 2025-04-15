package triphub.gateway.configs.gatewayConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtValidationFilterFactory extends AbstractGatewayFilterFactory<JwtValidationFilterFactory.Config> {

    private final WebClient webClient;
    private final String validationUrl;

    public JwtValidationFilterFactory(
        @Value("${spring.jwt-validation.security-service-url}") String validationUrl,
        @LoadBalanced WebClient.Builder webClientBuilder
    ) {
        super(Config.class);
        this.validationUrl = validationUrl;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Ваша текущая логика фильтра
            String path = exchange.getRequest().getPath().toString();
            if (path.startsWith("/api/auth") || path.startsWith("/api/profile") || path.matches("^/api/reports(/\\d+)?$")) {
                return chain.filter(exchange);
            }
            MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();

            return webClient.get()
                .uri(validationUrl)
                .cookies(c -> {
                    cookies.forEach((name, httpCookies) -> {
                        if (!httpCookies.isEmpty()) {
                            c.add(name, httpCookies.get(0).getValue());
                        }
                    });
                })
                .headers(headers -> headers.putAll(exchange.getRequest().getHeaders()))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                });
        };
    }
    public static class Config {
        // Конфигурационные параметры (если нужны)
    }
}
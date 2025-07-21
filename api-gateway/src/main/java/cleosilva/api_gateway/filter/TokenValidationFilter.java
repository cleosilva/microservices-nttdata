package cleosilva.api_gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.env.Environment; // Importe esta classe
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenValidationFilter implements GatewayFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${authentication.token}")
    private String validToken;

    // INJEÇÃO DO ENVIRONMENT PARA DEBUG
    private final Environment environment;

    // CONSTRUTOR PARA INJETAR ENVIRONMENT
    public TokenValidationFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        if (isPublicEndpoint(request.getURI().getPath())) {
            System.out.println("DEBUG: Endpoint público (" + request.getURI().getPath() + "). Permissão concedida.");
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);

        if(authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            System.out.println("DEBUG: Token ausente ou prefixo incorreto. Retornando 401.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!validToken.equals(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return path.contains("/actuator/health");
    }
}
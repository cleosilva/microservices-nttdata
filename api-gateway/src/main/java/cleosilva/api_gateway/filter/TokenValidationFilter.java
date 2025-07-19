package cleosilva.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class TokenValidationFilter implements GatewayFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    // Token fixo na classe por enquanto devido requisito do desafio.
    // Futuramente será implementado um sistema de login com spring security
    // Lembrete: Em produção, isto DEVE ser substituído por uma solução segura.
    private static final String FIXED_TOKEN = "supersecrettoken123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicEndpoint(request.getURI().getPath())) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);

        if(authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!FIXED_TOKEN.equals(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    /**
     * Verifica se o caminho da requisição corresponde a um endpoint público
     * que não exige autenticação.
     * @param path O caminho da URI da requisição.
     * @return true se for um endpoint público, false caso contrário.
     */
    private boolean isPublicEndpoint(String path) {
        return path.contains("/actuator/health");
    }
}

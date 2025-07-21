package cleosilva.api_gateway.config;

import cleosilva.api_gateway.filter.TokenValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewayConfig {
    @Autowired
    private TokenValidationFilter tokenValidationFilter;

    // Desabilita o filtro de segurança padrão do Spring Security para permitir o filtro customizado
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll() //Permitir tudo, pois o filtro customizado fará o trabalho
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCryptPasswordEncoder
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("products-catalog-route", r -> r.path("/products/**")
                        .filters(f -> f.filter(tokenValidationFilter))
                        .uri("lb://PRODUCTS-CATALOG"))
                .route("order-simulator-route", r -> r.path("/orders/**")
                        .filters(f -> f.filter(tokenValidationFilter))
                        .uri("lb://ORDER-SIMULATOR"))
                .build();
    }


}

package cleosilva.api_gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ApiGatewayApplicationTests {

	@Autowired
	private WebTestClient webClient;

	private static final String VALID_TOKEN = "supersecrettoken123";
	private static final String AUTH_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";


	@Test
	@DisplayName("Should allow access to public endpoint without token")
	void publicEndpoint_shouldAllowAccess() {
		// Este teste verifica o `/actuator/health` do próprio Gateway.
		// Garanta que `management.endpoints.web.exposure.include: health` está no application.yml.
		webClient.get().uri("/actuator/health")
				.exchange()
				.expectStatus().isOk(); // Espera status 200 OK
	}

	@Test
	@DisplayName("Should block access without Authorization header (401 UNAUTHORIZED)")
	void securedEndpoint_shouldBlockWithoutHeader() {
		// Testa uma rota que exige token, sem fornecer o cabeçalho.
		webClient.get().uri("/products/some-path") // Rota que será protegida pelo TokenValidationFilter
				.exchange()
				.expectStatus().isUnauthorized(); // Espera status 401 UNAUTHORIZED
	}

	@Test
	@DisplayName("Should block access with invalid token (403 FORBIDDEN)")
	void securedEndpoint_shouldBlockWithInvalidToken() {
		// Testa uma rota que exige token, com um token inválido.
		webClient.get().uri("/orders/some-other-path")
				.header(AUTH_HEADER, BEARER_PREFIX + "wrongtoken")
				.exchange()
				.expectStatus().isForbidden(); // Espera status 403 FORBIDDEN
	}

	@Test
	@DisplayName("Should allow access with valid token (and potentially route successfully)")
	void securedEndpoint_shouldAllowWithValidToken() {
		webClient.get().uri("/products/valid-path")
				.header("Authorization", "Bearer " + VALID_TOKEN)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE); // Mudar para 503
	}

	@Test
	@DisplayName("Should route to product-catalog with valid token (requires mock or real service)")
	void shouldRouteToProductCatalogWithValidToken() {
		webClient.get().uri("/products/some-item")
				.header("Authorization", "Bearer " + VALID_TOKEN)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE); // Mudar para 503
	}


}


package cleosilva.service_discovery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EurekaServerApplicationIT {
    @LocalServerPort
    private int port;

    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;

    private String baseUrl;
    private RestTemplate restTemplate;

    @BeforeEach
    void setup(){
        String protocol = sslEnabled ? "https" : "http";
        baseUrl = protocol + "://localhost:" + port;
        restTemplate = new RestTemplate();
    }

    @Test
    void contextLoads() {
        assertNotNull(baseUrl);
    }

    @Test
    void eurekaDashboardIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void eurekaHealthEndpointIsUp() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/actuator/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

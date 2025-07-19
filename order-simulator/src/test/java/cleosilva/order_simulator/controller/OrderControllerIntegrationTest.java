package cleosilva.order_simulator.controller;

import cleosilva.order_simulator.client.ProductCatalogFeignClient;
import cleosilva.order_simulator.dto.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductCatalogFeignClient productCatalogFeignClient;

    @Test
    @DisplayName("GET /orders/available-products should return a list of products")
    void listAvailableProducts_shouldReturnListOfProducts() throws Exception {
        ProductDto product1 = new ProductDto(1L, "Laptop", "High-end gaming laptop", new BigDecimal("5000.00"));
        ProductDto product2 = new ProductDto(2L, "Mouse", "Gaming mouse", new BigDecimal("150.00"));
        List<ProductDto> mockProducts = Arrays.asList(product1, product2);

        when(productCatalogFeignClient.listProducts()).thenReturn(mockProducts);

        mockMvc.perform(get("/orders/available-products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].price", is(150.00)));

        verify(productCatalogFeignClient, times(1)).listProducts();
    }

    @Test
    @DisplayName("GET /orders/available-products should return empty list if no products")
    void listAvailableProducts_shouldReturnEmptyList_whenNoProducts() throws Exception {
        when(productCatalogFeignClient.listProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/available-products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productCatalogFeignClient, times(1)).listProducts();
    }

    @Test
    @DisplayName("POST /orders/simulate should simulate order successfully")
    void simulateOrder_shouldReturnSimulatedOrderResponse() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 2L);

        ProductDto product1 = new ProductDto(1L, "Laptop", "High-end gaming laptop", new BigDecimal("5000.00"));
        ProductDto product2 = new ProductDto(2L, "Mouse", "Gaming mouse", new BigDecimal("150.00"));

        when(productCatalogFeignClient.findProductById(1L)).thenReturn(product1);
        when(productCatalogFeignClient.findProductById(2L)).thenReturn(product2);

        mockMvc.perform(post("/orders/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", is(5150.00)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.productsNotFound", hasSize(0)))
                .andExpect(jsonPath("$.items[0].productId", is(1)))
                .andExpect(jsonPath("$.items[0].quantity", is(1)))
                .andExpect(jsonPath("$.items[0].unitPrice", is(5000.00)));

        verify(productCatalogFeignClient, times(1)).findProductById(1L);
        verify(productCatalogFeignClient, times(1)).findProductById(2L);
    }

    @Test
    @DisplayName("POST /orders/simulate should handle products not found")
    void simulateOrder_shouldHandleProductsNotFound() throws Exception {
        List<Long> productIds = Arrays.asList(1L, 99L); // 99L é um ID não encontrado

        ProductDto product1 = new ProductDto(1L, "Laptop", "High-end gaming laptop", new BigDecimal("5000.00"));

        when(productCatalogFeignClient.findProductById(1L)).thenReturn(product1);
        when(productCatalogFeignClient.findProductById(99L)).thenReturn(null);

        mockMvc.perform(post("/orders/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", is(5000.00)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.productsNotFound", hasSize(1)))
                .andExpect(jsonPath("$.productsNotFound[0]", is(99)))
                .andExpect(jsonPath("$.items[0].productId", is(1)));

        verify(productCatalogFeignClient, times(1)).findProductById(1L);
        verify(productCatalogFeignClient, times(1)).findProductById(99L);
    }

    @Test
    @DisplayName("POST /orders/simulate with empty list should return empty response")
    void simulateOrder_withEmptyList_shouldReturnEmptyResponse() throws Exception {
        List<Long> emptyProductIds = Collections.emptyList();

        mockMvc.perform(post("/orders/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyProductIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", is(0)))
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.productsNotFound", hasSize(0)));


        verifyNoInteractions(productCatalogFeignClient);
    }

}

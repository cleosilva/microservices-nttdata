package cleosilva.microservices.product_catalog.controller;

import cleosilva.microservices.product_catalog.dto.ProductRequest;
import cleosilva.microservices.product_catalog.entity.Product;
import cleosilva.microservices.product_catalog.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product1;
    private Product product2;
    private ProductRequest validProductRequest;
    private ProductRequest invalidProductRequest;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        product1 = new Product("Smartphone X", "High-end smartphone", new BigDecimal("800.00"));
        product1 = productRepository.save(product1);

        product2 = new Product("Headphones", "Noise-cancelling headphones", new BigDecimal("150.00"));
        product2= productRepository.save(product2);

        validProductRequest = new ProductRequest();
        validProductRequest.setName("Smartwatch");
        validProductRequest.setDescription("Fitness tracker smartwatch");
        validProductRequest.setPrice(new BigDecimal("299.99"));

        invalidProductRequest = new ProductRequest();
        invalidProductRequest.setName(""); // Nome em branco para falhar validação
        invalidProductRequest.setDescription("Missing name");
        invalidProductRequest.setPrice(new BigDecimal("-10.00")); // Preço inválido
    }

    @Test
    @DisplayName("POST /products - Should create a new product and return 201 Created")
    void shouldCreateNewProduct() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name", is(validProductRequest.getName())))
                .andExpect(jsonPath("$.price", is(validProductRequest.getPrice().doubleValue())));
    }

    @Test
    @DisplayName("POST /products - Should return 400 Bad Request for invalid product data")
    void shouldReturnBadRequestForInvalidProductData() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists());
    }

    @Test
    @DisplayName("GET /products - Should return all products")
    void shouldReturnAllProducts() throws Exception {
        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(product1.getName())))
                .andExpect(jsonPath("$[1].name", is(product2.getName())));
    }

    @Test
    @DisplayName("GET /products - Should return 204 No Content if no products exist")
    void shouldReturnNoContentIfNoProductsExist() throws Exception {
        productRepository.deleteAll();

        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /products/{id} - Should return product by ID and 200 OK")
    void shouldReturnProductById() throws Exception {
        mockMvc.perform(get("/products/{id}", product1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) product1.getId())))
                .andExpect(jsonPath("$.name", is(product1.getName())));
    }

    @Test
    @DisplayName("GET /products/{id} - Should return 404 Not Found for non-existent product")
    void shouldReturnNotFoundForNonExistentProduct() throws Exception {
        mockMvc.perform(get("/products/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /products/{id} - Should update an existing product and return 200 OK")
    void shouldUpdateExistingProduct() throws Exception {
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Smartphone Updated");
        updateRequest.setDescription("Updated description");
        updateRequest.setPrice(new BigDecimal("850.00"));

        mockMvc.perform(put("/products/{id}", product1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) product1.getId())))
                .andExpect(jsonPath("$.name", is(updateRequest.getName())))
                .andExpect(jsonPath("$.price", is(updateRequest.getPrice().doubleValue())));
    }

    @Test
    @DisplayName("PUT /products/{id} - Should return 404 Not Found for updating non-existent product")
    void shouldReturnNotFoundForUpdatingNonExistentProduct() throws Exception {
        mockMvc.perform(put("/products/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProductRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /products/{id} - Should delete a product and return 204 No Content")
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/products/{id}", product1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


        assertFalse(productRepository.findById(product1.getId()).isPresent());
    }

    @Test
    @DisplayName("DELETE /products/{id} - Should return 404 Not Found for non-existent product")
    void shouldReturnNotFoundForDeletingNonExistentProduct() throws Exception {
        mockMvc.perform(delete("/products/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

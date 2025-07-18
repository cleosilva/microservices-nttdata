package cleosilva.microservices.product_catalog.repository;

import cleosilva.microservices.product_catalog.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        product = new Product("Smart TV", "4K HDR TV", new BigDecimal("2500.00"));
    }

    @Test
    @DisplayName("Should save a product successfully")
    void shouldSaveProductSuccessfully() {
        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId());
        assertEquals("Smart TV", savedProduct.getName());
        assertEquals(new BigDecimal("2500.00"), savedProduct.getPrice());

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getName(), foundProduct.get().getName());
    }

    @Test
    @DisplayName("Should find product by ID")
    void shouldFindProductById() {
        Product savedProduct = productRepository.save(product);

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getName(), foundProduct.get().getName());
    }

    @Test
    @DisplayName("Should return empty optional when product not found")
    void shouldReturnEmptyOptionalWhenProductNotFoundById() {
        Optional<Product> foundProduct = productRepository.findById(99L);
        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("Should list all products")
    void shouldListAllProducts() {
        productRepository.save(product);
        Product product2 = new Product("Soundbar", "Immersive audio", new BigDecimal("500.00"));
        productRepository.save(product2);

        List<Product> products = productRepository.findAll();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Smart TV")));
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Soundbar")));
    }

    @Test
    @DisplayName("Should update an existing product")
    void shouldUpdateExistingProduct() {
        Product savedProduct = productRepository.save(product);
        savedProduct.setName("Updated TV");
        savedProduct.setPrice(new BigDecimal("2600.00"));

        Product updatedProduct = productRepository.save(savedProduct);

        assertNotNull(updatedProduct);
        assertEquals("Updated TV", updatedProduct.getName());
        assertEquals(new BigDecimal("2600.00"), updatedProduct.getPrice());

        Optional<Product> foundProduct = productRepository.findById(updatedProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals("Updated TV", foundProduct.get().getName());

    }

    @Test
    @DisplayName("Should delete a product by ID")
    void shouldDeleteProductById() {
        Product savedProduct = productRepository.save(product);

        productRepository.deleteById(savedProduct.getId());

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("Should check if product exists by ID")
    void shouldCheckIfProductExistsById() {
        Product savedProduct = productRepository.save(product);

        assertTrue(productRepository.existsById(savedProduct.getId()));
        assertFalse(productRepository.existsById(99L));
    }

}

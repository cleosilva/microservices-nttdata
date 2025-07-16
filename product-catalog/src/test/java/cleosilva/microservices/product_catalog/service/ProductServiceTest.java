package cleosilva.microservices.product_catalog.service;

import cleosilva.microservices.product_catalog.dto.ProductRequest;
import cleosilva.microservices.product_catalog.entity.Product;
import cleosilva.microservices.product_catalog.exceptions.ResourceNotFoundException;
import cleosilva.microservices.product_catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp(){
        product = new Product("Laptop", "Powerful laptop", new BigDecimal("1200.00"));
        product.setId(1L);

        productRequest = new ProductRequest();
        productRequest.setName("Updated Laptop");
        productRequest.setDescription("Updated description");
        productRequest.setPrice(new BigDecimal("1300.00"));
    }

    @Test
    @DisplayName("Should create a product successfully")
    void shouldCreateProductSuccessfully() {

        ProductRequest requestToCreate = new ProductRequest();
        requestToCreate.setName("New Product Name");
        requestToCreate.setDescription("New Product Desc");
        requestToCreate.setPrice(new BigDecimal("100.00"));

        Product expectedSaveProduct = new Product("New Product Name", "New Product Desc", new BigDecimal("100.00"));
        expectedSaveProduct.setId(10L);

        when(productRepository.save(any(Product.class))).thenReturn(expectedSaveProduct);

        Product createProduct = productService.createProduct(requestToCreate);

        assertNotNull(createProduct);
        assertEquals(expectedSaveProduct.getId(), createProduct.getId());
        assertEquals(expectedSaveProduct.getName(), createProduct.getName());
        assertEquals(expectedSaveProduct.getPrice(), createProduct.getPrice());

        verify(productRepository, times(1)).save(any(Product.class));
    }
    @Test
    @DisplayName("Should list all products")
    void shouldListAllProducts() {
        Product product2 = new Product("Mouse", "Wireless mouse", new BigDecimal("50.00"));
        product2.setId(2L);
        List<Product> productList = Arrays.asList(product, product2);

        when(productRepository.findAll()).thenReturn(productList);

        List<Product> foundProducts = productService.listProducts();

        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
        assertEquals("Laptop", foundProducts.getFirst().getName());
        assertEquals("Mouse", foundProducts.get(1).getName());

        verify(productRepository, times(1)).findAll();
    }
    @Test
    @DisplayName("Should get product by ID when found")
    void shouldGetProductByIdWhenFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals("Laptop", foundProduct.getName());
        assertEquals(1L, foundProduct.getId());

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product by ID is not found")
    void shouldThrowResourceNotFoundExceptionWhenProductByIdNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));

        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should update an existing product")
    void shouldUpdateExistingProduct(){
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProduct(1L, productRequest);
        updatedProduct.setId(1L);

        assertNotNull(updatedProduct);
        assertEquals("Updated Laptop", updatedProduct.getName());
        assertEquals(new BigDecimal("1300.00"), updatedProduct.getPrice());
        assertEquals(1L, updatedProduct.getId());

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResouceNotFoundException when trying to update non-existent product")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentProduct(){
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                productService.updateProduct(99L, productRequest)
        );

        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete a product successfully")
    void shouldDeleteProductSuccessfully() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete non-existent product")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentProduct() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));

        verify(productRepository, times(1)).existsById(99L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}

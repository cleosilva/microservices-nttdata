package cleosilva.order_simulator.service;

import cleosilva.order_simulator.client.ProductCatalogFeignClient;
import cleosilva.order_simulator.dto.ProductDto;
import cleosilva.order_simulator.dto.OrderItemDto;
import cleosilva.order_simulator.dto.SimulatedOrderResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o Mockito para JUnit 5
class OrderServiceUnitTest {

    @Mock
    private ProductCatalogFeignClient productCatalogFeignClient;

    @InjectMocks
    private OrderService orderService;


    private ProductDto product1;
    private ProductDto product2;

    @BeforeEach
    void setUp() {

        product1 = new ProductDto(1L, "Laptop", "High-end gaming laptop", new BigDecimal("5000.00"));
        product2 = new ProductDto(2L, "Mouse", "Gaming mouse", new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Should simulate order with existing products")
    void simulateOrder_withExistingProducts_shouldReturnCorrectResponse() {

        List<Long> productIds = Arrays.asList(1L, 2L);


        when(productCatalogFeignClient.findProductById(1L)).thenReturn(product1);

        when(productCatalogFeignClient.findProductById(2L)).thenReturn(product2);


        SimulatedOrderResponseDto response = orderService.simulateOrder(productIds);


        assertNotNull(response);
        assertEquals(2, response.items().size());
        assertEquals(new BigDecimal("5150.00"), response.totalAmount());
        assertTrue(response.productsNotFound().isEmpty());


        OrderItemDto item1 = response.items().get(0);
        assertEquals(1L, item1.productId());
        assertEquals(1, item1.quantity());
        assertEquals(new BigDecimal("5000.00"), item1.unitPrice());

        OrderItemDto item2 = response.items().get(1);
        assertEquals(2L, item2.productId());
        assertEquals(1, item2.quantity());
        assertEquals(new BigDecimal("150.00"), item2.unitPrice());


        verify(productCatalogFeignClient, times(1)).findProductById(1L);
        verify(productCatalogFeignClient, times(1)).findProductById(2L);
    }

    @Test
    @DisplayName("Should simulate order with some products not found")
    void simulateOrder_withSomeProductsNotFound_shouldReturnCorrectResponse() {

        List<Long> productIds = Arrays.asList(1L, 3L); // 3L não existe


        when(productCatalogFeignClient.findProductById(1L)).thenReturn(product1);
        when(productCatalogFeignClient.findProductById(3L)).thenReturn(null); // Simula produto não encontrado

        SimulatedOrderResponseDto response = orderService.simulateOrder(productIds);

        assertNotNull(response);
        assertEquals(1, response.items().size()); // Apenas o produto 1 deve estar na lista
        assertEquals(new BigDecimal("5000.00"), response.totalAmount()); // Apenas o preço do produto 1
        assertEquals(1, response.productsNotFound().size()); // O produto 3L deve estar na lista de não encontrados
        assertTrue(response.productsNotFound().contains(3L));

        verify(productCatalogFeignClient, times(1)).findProductById(1L);
        verify(productCatalogFeignClient, times(1)).findProductById(3L);
    }

    @Test
    @DisplayName("Should simulate order with no products found")
    void simulateOrder_withNoProductsFound_shouldReturnEmptyItemsAndZeroTotal() {

        List<Long> productIds = Arrays.asList(4L, 5L);


        when(productCatalogFeignClient.findProductById(anyLong())).thenReturn(null); // Qualquer ID retorna null

        SimulatedOrderResponseDto response = orderService.simulateOrder(productIds);

        assertNotNull(response);
        assertTrue(response.items().isEmpty());
        assertEquals(BigDecimal.ZERO, response.totalAmount());
        assertEquals(2, response.productsNotFound().size());
        assertTrue(response.productsNotFound().containsAll(Arrays.asList(4L, 5L)));

        verify(productCatalogFeignClient, times(1)).findProductById(4L);
        verify(productCatalogFeignClient, times(1)).findProductById(5L);
    }

    @Test
    @DisplayName("Should handle empty productIds list gracefully")
    void simulateOrder_withEmptyProductIdsList_shouldReturnEmptyResponse() {
        List<Long> productIds = Collections.emptyList();

        SimulatedOrderResponseDto response = orderService.simulateOrder(productIds);

        assertNotNull(response);
        assertTrue(response.items().isEmpty());
        assertEquals(BigDecimal.ZERO, response.totalAmount());
        assertTrue(response.productsNotFound().isEmpty());


        verifyNoInteractions(productCatalogFeignClient);
    }


    @Test
    @DisplayName("Should return all available products")
    void listAvailableProducts_shouldReturnAllProductsFromCatalog() {

        List<ProductDto> mockProducts = Arrays.asList(product1, product2);
        when(productCatalogFeignClient.listProducts()).thenReturn(mockProducts);

        List<ProductDto> result = orderService.listAvailableProducts();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(product1, result.get(0));
        assertEquals(product2, result.get(1));

        verify(productCatalogFeignClient, times(1)).listProducts();
    }

    @Test
    @DisplayName("Should return empty list if no products available")
    void listAvailableProducts_shouldReturnEmptyListIfNoProducts() {
        when(productCatalogFeignClient.listProducts()).thenReturn(Collections.emptyList());

        List<ProductDto> result = orderService.listAvailableProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productCatalogFeignClient, times(1)).listProducts();
    }
}
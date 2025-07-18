package cleosilva.order_simulator.service;

import cleosilva.order_simulator.client.ProductCatalogFeignClient;
import cleosilva.order_simulator.dto.OrderItemDto;
import cleosilva.order_simulator.dto.ProductDto;
import cleosilva.order_simulator.dto.SimulatedOrderResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private ProductCatalogFeignClient productCatalogFeignClient;

    public SimulatedOrderResponseDto simulateOrder(List<Long> productIds) {
        List<OrderItemDto> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Long> productsNotFound = new ArrayList<>();

        for (Long productId : productIds) {
            ProductDto product = productCatalogFeignClient.findProductById(productId);
            if (product != null) {
                OrderItemDto itemDto = new OrderItemDto(product.id(), 1, product.price());
                items.add(itemDto);
                totalAmount = totalAmount.add(product.price());
            } else {
                productsNotFound.add(productId);
            }
        }

        return new SimulatedOrderResponseDto(items,totalAmount,productsNotFound);
    }

    public List<ProductDto> listAvailableProducts() {
        return productCatalogFeignClient.listProducts();
    }
}

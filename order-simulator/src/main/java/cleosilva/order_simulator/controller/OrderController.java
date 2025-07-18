package cleosilva.order_simulator.controller;

import cleosilva.order_simulator.dto.ProductDto;
import cleosilva.order_simulator.dto.SimulatedOrderResponseDto;
import cleosilva.order_simulator.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/simulate")
    public ResponseEntity<SimulatedOrderResponseDto> simulateOrder(@RequestBody List<Long> productIds) {
        SimulatedOrderResponseDto response = orderService.simulateOrder(productIds);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-products")
    public ResponseEntity<List<ProductDto>> getAvailableProducts() {
        List<ProductDto> products = orderService.listAvailableProducts();
        return ResponseEntity.ok(products);
    }
}

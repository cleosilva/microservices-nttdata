package cleosilva.order_simulator.controller;

import cleosilva.order_simulator.dto.ProductDto;
import cleosilva.order_simulator.dto.SimulatedOrderResponseDto;
import cleosilva.order_simulator.service.OrderEventPublisher;
import cleosilva.order_simulator.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    private final OrderEventPublisher orderEventPublisher;

    public OrderController(OrderEventPublisher orderEventPublisher) {
        this.orderEventPublisher = orderEventPublisher;
    }

    @PostMapping("/simulate")
    public ResponseEntity<SimulatedOrderResponseDto> simulateOrder(@RequestBody List<Long> productIds) {
        SimulatedOrderResponseDto response = orderService.simulateOrder(productIds);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String orderEventDetails = objectMapper.writeValueAsString(response);

            orderEventPublisher.publishOrderCreatedEvent(orderEventDetails);

            System.out.println("Evento de pedido simulado publicado no RabbitMQ: " + orderEventDetails);

        } catch (Exception e){
            // Logar o erro, mas não impedir a resposta da API se a simulação foi bem-sucedida
            System.err.println("Erro ao publicar evento de pedido no RabbitMQ: " + e.getMessage());
            // Considere um mecanismo de retry ou DLQ (Dead Letter Queue) para produção
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-products")
    public ResponseEntity<List<ProductDto>> getAvailableProducts() {
        List<ProductDto> products = orderService.listAvailableProducts();
        return ResponseEntity.ok(products);
    }
}

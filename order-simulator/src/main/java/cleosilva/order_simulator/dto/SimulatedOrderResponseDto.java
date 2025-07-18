package cleosilva.order_simulator.dto;

import java.math.BigDecimal;
import java.util.List;

public record SimulatedOrderResponseDto(List<OrderItemDto> items, BigDecimal totalAmount, List<Long> productsNotFound) {}

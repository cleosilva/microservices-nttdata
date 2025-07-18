package cleosilva.order_simulator.dto;

import java.math.BigDecimal;

public record OrderItemDto(Long productId, int quantity, BigDecimal unitPrice){ }

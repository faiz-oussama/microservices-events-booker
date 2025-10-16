package com.ticketing.ticketservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long ticketId;
    private Integer quantity;
    private BigDecimal price;
}
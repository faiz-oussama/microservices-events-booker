package com.ticketing.ticketservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TicketDTO {
    private Long id;
    private Long eventId;
    private String eventName;
    private String venue;
    private BigDecimal price;
    private Boolean available;
    private Long userId;
}
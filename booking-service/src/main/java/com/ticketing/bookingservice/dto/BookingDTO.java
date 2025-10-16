package com.ticketing.bookingservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long ticketId;
    private String ticketName;
    private String eventName;
    private Integer quantity;
    private BigDecimal totalAmount;
    private BigDecimal unitPrice;
    private String status;
    private LocalDateTime bookingDate;
    private LocalDateTime expiryDate;
    private LocalDateTime confirmedDate;
    private LocalDateTime cancelledDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
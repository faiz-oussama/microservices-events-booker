package com.ticketing.bookingservice.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class BookingCreateDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Ticket ID is required")
    private Long ticketId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
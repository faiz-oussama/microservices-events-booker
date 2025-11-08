package com.ticketing.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "ticket-service")
public interface TicketServiceClient {

    @GetMapping("/api/tickets/{id}")
    TicketDTO getTicketById(@PathVariable("id") Long id);

    @GetMapping("/api/tickets/available")
    java.util.List<TicketDTO> getAvailableTickets();

    @PutMapping("/api/tickets/{id}/reserve")
    void reserveTickets(@PathVariable("id") Long ticketId, @RequestParam Integer quantity);

    @PutMapping("/api/tickets/{id}/release")
    void releaseTickets(@PathVariable("id") Long ticketId, @RequestParam Integer quantity);

    // DTO for ticket information
    record TicketDTO(
        Long id,
        Long eventId,
        String eventName,
        String venue,
        BigDecimal price,
        Boolean available,
        Long userId
    ) {}
}
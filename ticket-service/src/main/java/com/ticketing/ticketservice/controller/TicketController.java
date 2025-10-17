package com.ticketing.ticketservice.controller;

import com.ticketing.ticketservice.model.Ticket;
import com.ticketing.ticketservice.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }
    
    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id).orElse(null);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Ticket createTicket(@RequestBody Ticket ticket) {
        return ticketService.createTicket(ticket);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Ticket updateTicket(@PathVariable Long id, @RequestBody Ticket ticketDetails) {
        return ticketService.updateTicket(id, ticketDetails);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }
    
    @GetMapping("/available")
    public List<Ticket> getAvailableTickets() {
        return ticketService.getAvailableTickets();
    }
    
    @GetMapping("/event/{eventId}")
    public List<Ticket> getTicketsByEventId(@PathVariable Long eventId) {
        return ticketService.getTicketsByEventId(eventId);
    }
    
    @GetMapping("/user/{userId}")
    public List<Ticket> getTicketsByUserId(@PathVariable Long userId) {
        return ticketService.getTicketsByUserId(userId);
    }
}
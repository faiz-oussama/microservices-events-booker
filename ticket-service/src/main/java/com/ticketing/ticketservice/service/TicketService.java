package com.ticketing.ticketservice.service;

import com.ticketing.ticketservice.model.Ticket;
import com.ticketing.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
    
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }
    
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
    
    public Ticket updateTicket(Long id, Ticket ticketDetails) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticket.setEventName(ticketDetails.getEventName());
            ticket.setVenue(ticketDetails.getVenue());
            ticket.setPrice(ticketDetails.getPrice());
            ticket.setAvailable(ticketDetails.getAvailable());
            ticket.setUserId(ticketDetails.getUserId());
            return ticketRepository.save(ticket);
        }
        return null;
    }
    
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }
    
    public List<Ticket> getAvailableTickets() {
        return ticketRepository.findByAvailableTrue();
    }
    
    public List<Ticket> getTicketsByEventId(Long eventId) {
        return ticketRepository.findByEventId(eventId);
    }
    
    public List<Ticket> getTicketsByUserId(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
}
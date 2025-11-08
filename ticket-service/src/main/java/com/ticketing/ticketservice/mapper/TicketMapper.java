package com.ticketing.ticketservice.mapper;

import com.ticketing.ticketservice.dto.TicketDTO;
import com.ticketing.ticketservice.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {
    
    public TicketDTO toDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setEventId(ticket.getEventId());
        dto.setEventName(ticket.getEventName());
        dto.setVenue(ticket.getVenue());
        dto.setPrice(ticket.getPrice());
        dto.setAvailable(ticket.getAvailable());
        dto.setUserId(ticket.getUserId());
        return dto;
    }
    
    public Ticket toEntity(TicketDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setId(dto.getId());
        ticket.setEventId(dto.getEventId());
        ticket.setEventName(dto.getEventName());
        ticket.setVenue(dto.getVenue());
        ticket.setPrice(dto.getPrice());
        ticket.setAvailable(dto.getAvailable());
        ticket.setUserId(dto.getUserId());
        return ticket;
    }
}
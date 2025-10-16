package com.ticketing.ticketservice.repository;

import com.ticketing.ticketservice.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEventId(Long eventId);
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByAvailableTrue();
}
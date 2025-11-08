package com.ticketing.ticketservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long eventId;
    
    private String eventName;
    
    private String venue;
    
    private BigDecimal price;
    
    private Boolean available;
    
    private Long userId;
}
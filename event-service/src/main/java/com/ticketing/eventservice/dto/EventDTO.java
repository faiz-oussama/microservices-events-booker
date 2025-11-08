package com.ticketing.eventservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Long id;
    private String name;
    private String venue;
    private LocalDateTime dateTime;
    private String description;
    private Long organizerId;
    private String organizerName; // From auth-service
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
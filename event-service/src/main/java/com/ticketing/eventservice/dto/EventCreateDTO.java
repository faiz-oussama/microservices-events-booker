package com.ticketing.eventservice.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class EventCreateDTO {

    @NotBlank(message = "Event name is required")
    @Size(min = 2, max = 100, message = "Event name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Venue is required")
    @Size(min = 2, max = 200, message = "Venue must be between 2 and 200 characters")
    private String venue;

    @NotNull(message = "Event date and time is required")
    private LocalDateTime dateTime;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Organizer ID is required")
    private Long organizerId;
}
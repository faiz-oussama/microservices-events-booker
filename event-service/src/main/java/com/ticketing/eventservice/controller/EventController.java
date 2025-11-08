package com.ticketing.eventservice.controller;

import com.ticketing.eventservice.dto.EventCreateDTO;
import com.ticketing.eventservice.dto.EventDTO;
import com.ticketing.eventservice.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventCreateDTO createDTO) {
        try {
            EventDTO createdEvent = eventService.createEvent(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create event: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while creating event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        log.info("Received request to get all events");
        try {
            List<EventDTO> events = eventService.getAllEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Failed to fetch events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        log.info("Received request to get event with ID: {}", id);
        try {
            return eventService.getEventById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to fetch event with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @Valid @RequestBody EventCreateDTO updateDTO) {
        log.info("Received request to update event with ID: {}", id);
        try {
            EventDTO updatedEvent = eventService.updateEvent(id, updateDTO);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update event: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while updating event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.info("Received request to delete event with ID: {}", id);
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete event: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error while deleting event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventDTO>> getEventsByOrganizer(@PathVariable Long organizerId) {
        log.info("Received request to get events for organizer ID: {}", organizerId);
        try {
            List<EventDTO> events = eventService.getEventsByOrganizer(organizerId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Failed to fetch events for organizer ID: {}", organizerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDTO>> getUpcomingEvents() {
        log.info("Received request to get upcoming events");
        try {
            List<EventDTO> events = eventService.getUpcomingEvents();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Failed to fetch upcoming events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EventDTO>> getEventsByStatus(@PathVariable String status) {
        log.info("Received request to get events with status: {}", status);
        try {
            List<EventDTO> events = eventService.getEventsByStatus(status);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Failed to fetch events with status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEventsByVenue(@RequestParam String venue) {
        log.info("Received request to search events by venue: {}", venue);
        try {
            List<EventDTO> events = eventService.searchEventsByVenue(venue);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Failed to search events by venue: {}", venue, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
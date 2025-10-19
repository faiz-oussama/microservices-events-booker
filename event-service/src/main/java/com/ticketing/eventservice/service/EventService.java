package com.ticketing.eventservice.service;

import com.ticketing.eventservice.client.AuthServiceClient;
import com.ticketing.eventservice.dto.EventCreateDTO;
import com.ticketing.eventservice.dto.EventDTO;
import com.ticketing.eventservice.mapper.EventMapper;
import com.ticketing.eventservice.model.Event;
import com.ticketing.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final AuthServiceClient authServiceClient;

    public EventDTO createEvent(EventCreateDTO createDTO) {
        log.info("Creating new event: {}", createDTO.getName());

        Boolean userExists = authServiceClient.validateUser(createDTO.getOrganizerId());
        if (!userExists) {
            throw new IllegalArgumentException("Organizer with ID " + createDTO.getOrganizerId() + " does not exist");
        }

        // Validate event date is in the future
        if (createDTO.getDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date must be in the future");
        }

        Event event = eventMapper.toEntity(createDTO);
        Event savedEvent = eventRepository.save(event);

        log.info("Event created successfully with ID: {}", savedEvent.getId());
        return eventMapper.toDTO(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents() {
        log.info("Fetching all events");
        return eventRepository.findAll().stream()
                .map(this::enrichEventWithOrganizerName)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EventDTO> getEventById(Long id) {
        log.info("Fetching event with ID: {}", id);
        return eventRepository.findById(id)
                .map(this::enrichEventWithOrganizerName);
    }

    public EventDTO updateEvent(Long id, EventCreateDTO updateDTO) {
        log.info("Updating event with ID: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));

        // Validate organizer exists if changed
        if (!event.getOrganizerId().equals(updateDTO.getOrganizerId())) {
            Boolean userExists = authServiceClient.validateUser(updateDTO.getOrganizerId());
            if (!userExists) {
                throw new IllegalArgumentException("Organizer with ID " + updateDTO.getOrganizerId() + " does not exist");
            }
        }

        // Validate event date is in the future
        if (updateDTO.getDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date must be in the future");
        }

        eventMapper.updateEntityFromDTO(event, updateDTO);
        Event updatedEvent = eventRepository.save(event);

        log.info("Event updated successfully with ID: {}", updatedEvent.getId());
        return eventMapper.toDTO(updatedEvent);
    }

    public void deleteEvent(Long id) {
        log.info("Deleting event with ID: {}", id);

        if (!eventRepository.existsById(id)) {
            throw new IllegalArgumentException("Event not found with ID: " + id);
        }

        eventRepository.deleteById(id);
        log.info("Event deleted successfully with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByOrganizer(Long organizerId) {
        log.info("Fetching events for organizer ID: {}", organizerId);
        return eventRepository.findByOrganizerId(organizerId).stream()
                .map(this::enrichEventWithOrganizerName)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getUpcomingEvents() {
        log.info("Fetching upcoming events");
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findUpcomingEvents(now).stream()
                .map(this::enrichEventWithOrganizerName)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByStatus(String status) {
        log.info("Fetching events with status: {}", status);
        return eventRepository.findByStatus(status).stream()
                .map(this::enrichEventWithOrganizerName)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventDTO> searchEventsByVenue(String venue) {
        log.info("Searching events by venue: {}", venue);
        return eventRepository.findByVenueContainingIgnoreCase(venue).stream()
                .map(this::enrichEventWithOrganizerName)
                .collect(Collectors.toList());
    }

    private EventDTO enrichEventWithOrganizerName(Event event) {
        EventDTO dto = eventMapper.toDTO(event);
        try {
            String organizerName = authServiceClient.getUserName(event.getOrganizerId());
            dto.setOrganizerName(organizerName);
        } catch (Exception e) {
            log.warn("Failed to fetch organizer name for user ID: {}", event.getOrganizerId(), e);
            dto.setOrganizerName("Unknown Organizer");
        }
        return dto;
    }
}
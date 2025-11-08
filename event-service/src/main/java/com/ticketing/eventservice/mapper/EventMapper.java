package com.ticketing.eventservice.mapper;

import com.ticketing.eventservice.dto.EventCreateDTO;
import com.ticketing.eventservice.dto.EventDTO;
import com.ticketing.eventservice.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventDTO toDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setVenue(event.getVenue());
        dto.setDateTime(event.getDateTime());
        dto.setDescription(event.getDescription());
        dto.setOrganizerId(event.getOrganizerId());
        dto.setStatus(event.getStatus());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        return dto;
    }

    public Event toEntity(EventCreateDTO createDTO) {
        Event event = new Event();
        event.setName(createDTO.getName());
        event.setVenue(createDTO.getVenue());
        event.setDateTime(createDTO.getDateTime());
        event.setDescription(createDTO.getDescription());
        event.setOrganizerId(createDTO.getOrganizerId());
        event.setStatus("ACTIVE");
        return event;
    }

    public void updateEntityFromDTO(Event event, EventCreateDTO updateDTO) {
        event.setName(updateDTO.getName());
        event.setVenue(updateDTO.getVenue());
        event.setDateTime(updateDTO.getDateTime());
        event.setDescription(updateDTO.getDescription());
        event.setOrganizerId(updateDTO.getOrganizerId());
    }
}
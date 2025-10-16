package com.ticketing.eventservice.repository;

import com.ticketing.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizerId(Long organizerId);

    List<Event> findByStatus(String status);

    @Query("SELECT e FROM Event e WHERE e.dateTime >= :startDate AND e.dateTime <= :endDate")
    List<Event> findEventsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM Event e WHERE e.dateTime >= :currentDate ORDER BY e.dateTime ASC")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDateTime currentDate);

    List<Event> findByVenueContainingIgnoreCase(String venue);
}
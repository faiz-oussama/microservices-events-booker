package com.ticketing.bookingservice.service;

import com.ticketing.bookingservice.client.AuthServiceClient;
import com.ticketing.bookingservice.client.TicketServiceClient;
import com.ticketing.bookingservice.dto.BookingCreateDTO;
import com.ticketing.bookingservice.dto.BookingDTO;
import com.ticketing.bookingservice.model.Booking;
import com.ticketing.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AuthServiceClient authServiceClient;
    private final TicketServiceClient ticketServiceClient;

    private static final int RESERVATION_EXPIRY_MINUTES = 15;

    public BookingDTO reserveBooking(BookingCreateDTO createDTO) {
        log.info("Reserving booking for user {} and ticket {}", createDTO.getUserId(), createDTO.getTicketId());

        Boolean userExists = authServiceClient.validateUser(createDTO.getUserId());
        if (!userExists) {
            throw new IllegalArgumentException("User with ID " + createDTO.getUserId() + " does not exist");
        }

        // Get ticket information
        TicketServiceClient.TicketDTO ticket = ticketServiceClient.getTicketById(createDTO.getTicketId());
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket with ID " + createDTO.getTicketId() + " does not exist");
        }

        // Check if user already has an active reservation for this ticket
        boolean hasActiveReservation = bookingRepository.existsByUserIdAndTicketIdAndStatus(
            createDTO.getUserId(), createDTO.getTicketId(), "RESERVED");
        if (hasActiveReservation) {
            throw new IllegalArgumentException("User already has an active reservation for this ticket");
        }

        // Check ticket availability
        Long activeBookings = bookingRepository.sumQuantityByTicketIdAndActiveStatus(createDTO.getTicketId());
        if (activeBookings + createDTO.getQuantity() > 1000) { // Assuming max 1000 tickets per event
            throw new IllegalArgumentException("Not enough tickets available");
        }

        // Reserve tickets in ticket service
        try {
            ticketServiceClient.reserveTickets(createDTO.getTicketId(), createDTO.getQuantity());
        } catch (Exception e) {
            log.error("Failed to reserve tickets in ticket service", e);
            throw new IllegalArgumentException("Failed to reserve tickets");
        }

        Booking booking = new Booking();
        booking.setUserId(createDTO.getUserId());
        booking.setTicketId(createDTO.getTicketId());
        booking.setQuantity(createDTO.getQuantity());
        booking.setTotalAmount(ticket.price().multiply(BigDecimal.valueOf(createDTO.getQuantity())));
        booking.setStatus("RESERVED");
        booking.setExpiryDate(LocalDateTime.now().plusMinutes(RESERVATION_EXPIRY_MINUTES));

        Booking savedBooking = bookingRepository.save(booking);

        log.info("Booking reserved successfully with ID: {}", savedBooking.getId());
        return enrichBookingWithDetails(savedBooking);
    }

    public BookingDTO confirmBooking(Long bookingId) {
        log.info("Confirming booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        if (!"RESERVED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Only reserved bookings can be confirmed");
        }

        if (booking.getExpiryDate().isBefore(LocalDateTime.now())) {
            expireBooking(booking);
            throw new IllegalArgumentException("Booking has expired");
        }

        booking.setStatus("CONFIRMED");
        booking.setConfirmedDate(LocalDateTime.now());
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking confirmed successfully with ID: {}", bookingId);
        return enrichBookingWithDetails(updatedBooking);
    }

    public BookingDTO cancelBooking(Long bookingId) {
        log.info("Cancelling booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        if ("CANCELLED".equals(booking.getStatus()) || "EXPIRED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is already cancelled or expired");
        }

        try {
            ticketServiceClient.releaseTickets(booking.getTicketId(), booking.getQuantity());
        } catch (Exception e) {
            log.error("Failed to release tickets in ticket service", e);
        }

        booking.setStatus("CANCELLED");
        booking.setCancelledDate(LocalDateTime.now());
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking cancelled successfully with ID: {}", bookingId);
        return enrichBookingWithDetails(updatedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> getUserBookings(Long userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        return bookingRepository.findBookingHistoryByUserId(userId).stream()
                .map(this::enrichBookingWithDetails)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<BookingDTO> getBookingById(Long bookingId) {
        log.info("Fetching booking with ID: {}", bookingId);
        return bookingRepository.findById(bookingId)
                .map(this::enrichBookingWithDetails);
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByStatus(String status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status).stream()
                .map(this::enrichBookingWithDetails)
                .collect(Collectors.toList());
    }

    public void processExpiredReservations() {
        log.info("Processing expired reservations");
        List<Booking> expiredBookings = bookingRepository.findExpiredReservations(LocalDateTime.now());

        for (Booking booking : expiredBookings) {
            try {
                expireBooking(booking);
            } catch (Exception e) {
                log.error("Failed to expire booking with ID: {}", booking.getId(), e);
            }
        }
    }

    private void expireBooking(Booking booking) {
        log.info("Expiring booking with ID: {}", booking.getId());

        try {
            ticketServiceClient.releaseTickets(booking.getTicketId(), booking.getQuantity());
        } catch (Exception e) {
            log.error("Failed to release tickets for expired booking", e);
        }

        booking.setStatus("EXPIRED");
        bookingRepository.save(booking);
    }

    private BookingDTO enrichBookingWithDetails(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setTicketId(booking.getTicketId());
        dto.setQuantity(booking.getQuantity());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus());
        dto.setBookingDate(booking.getBookingDate());
        dto.setExpiryDate(booking.getExpiryDate());
        dto.setConfirmedDate(booking.getConfirmedDate());
        dto.setCancelledDate(booking.getCancelledDate());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        try {
            String userName = authServiceClient.getUserName(booking.getUserId());
            dto.setUserName(userName);
        } catch (Exception e) {
            log.warn("Failed to fetch user name for user ID: {}", booking.getUserId(), e);
            dto.setUserName("Unknown User");
        }

        try {
            TicketServiceClient.TicketDTO ticket = ticketServiceClient.getTicketById(booking.getTicketId());
            if (ticket != null) {
                dto.setTicketName(ticket.eventName() + " - " + ticket.venue());
                dto.setEventName(ticket.eventName());
                dto.setUnitPrice(ticket.price());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch ticket details for ticket ID: {}", booking.getTicketId(), e);
            dto.setTicketName("Unknown Ticket");
            dto.setEventName("Unknown Event");
        }

        return dto;
    }
}
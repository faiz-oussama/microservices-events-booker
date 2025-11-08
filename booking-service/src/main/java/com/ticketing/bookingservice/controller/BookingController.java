package com.ticketing.bookingservice.controller;

import com.ticketing.bookingservice.dto.BookingCreateDTO;
import com.ticketing.bookingservice.dto.BookingDTO;
import com.ticketing.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/reserve")
    public ResponseEntity<BookingDTO> reserveBooking(@Valid @RequestBody BookingCreateDTO createDTO) {
        try {
            BookingDTO booking = bookingService.reserveBooking(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (IllegalArgumentException e) {
            log.error("Failed to reserve booking: {} - {}", createDTO, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while reserving booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingDTO> confirmBooking(@PathVariable Long id) {

        try {
            BookingDTO booking = bookingService.confirmBooking(id);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            log.error("Failed to confirm booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while confirming booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id) {
        log.info("Received request to cancel booking with ID: {}", id);
        try {
            BookingDTO booking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            log.error("Failed to cancel booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while cancelling booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        log.info("Received request to get booking with ID: {}", id);
        try {
            return bookingService.getBookingById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to fetch booking with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        log.info("Received request to get bookings for user ID: {}", userId);
        try {
            List<BookingDTO> bookings = bookingService.getUserBookings(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Failed to fetch bookings for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDTO>> getBookingsByStatus(@PathVariable String status) {
        log.info("Received request to get bookings with status: {}", status);
        try {
            List<BookingDTO> bookings = bookingService.getBookingsByStatus(status);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Failed to fetch bookings with status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/process-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> processExpiredReservations() {
        log.info("Received request to process expired reservations");
        try {
            bookingService.processExpiredReservations();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to process expired reservations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
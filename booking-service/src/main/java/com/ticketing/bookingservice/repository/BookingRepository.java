package com.ticketing.bookingservice.repository;

import com.ticketing.bookingservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByTicketId(Long ticketId);

    List<Booking> findByStatus(String status);

    List<Booking> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT b FROM Booking b WHERE b.expiryDate < :currentDate AND b.status = 'RESERVED'")
    List<Booking> findExpiredReservations(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId ORDER BY b.bookingDate DESC")
    List<Booking> findBookingHistoryByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.ticketId = :ticketId AND b.status IN ('RESERVED', 'CONFIRMED')")
    Long countActiveBookingsByTicketId(@Param("ticketId") Long ticketId);

    @Query("SELECT SUM(b.quantity) FROM Booking b WHERE b.ticketId = :ticketId AND b.status IN ('RESERVED', 'CONFIRMED')")
    Long sumQuantityByTicketIdAndActiveStatus(@Param("ticketId") Long ticketId);

    boolean existsByUserIdAndTicketIdAndStatus(Long userId, Long ticketId, String status);
}
package com.example.demo.tickettype.repository;

import com.example.demo.tickettype.domain.TicketType;
import com.example.demo.tickettype.domain.TicketTypeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {

    List<TicketType> findByEventId(Long eventId);

    boolean existsByEventIdAndStatus(Long eventId, TicketTypeStatus status);

    Optional<TicketType> findByIdAndEventId(Long id, Long eventId);

    @Query("""
            SELECT COALESCE(SUM(t.capacity - t.registeredCount), 0)
            FROM TicketType t
            WHERE t.event.id = :eventId AND t.status = :status
            """)
    long sumAvailableSlots(@Param("eventId") Long eventId, @Param("status") TicketTypeStatus status);
}

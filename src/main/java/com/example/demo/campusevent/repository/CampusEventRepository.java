package com.example.demo.campusevent.repository;

import com.example.demo.campusevent.domain.CampusEvent;
import com.example.demo.campusevent.domain.EventCategory;
import com.example.demo.campusevent.domain.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;

public interface CampusEventRepository extends JpaRepository<CampusEvent, Long> {

    @Query("""
            SELECT e FROM CampusEvent e
            WHERE e.status = :status
              AND e.eventDate > :from
              AND (:category IS NULL OR e.category = :category)
            """)
    Page<CampusEvent> searchPublished(
            @Param("status") EventStatus status,
            @Param("from") ZonedDateTime from,
            @Param("category") EventCategory category,
            Pageable pageable
    );
}

package com.example.demo.campusevent.repository;

import com.example.demo.campusevent.domain.CampusEvent;
import com.example.demo.campusevent.domain.EventCategory;
import com.example.demo.campusevent.domain.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface CampusEventRepository extends JpaRepository<CampusEvent, Long> {

    Optional<CampusEvent> findByIdAndOrganizerId(Long id, Long organizerId);

    Page<CampusEvent> findByStatusAndEventDateAfter(
            EventStatus status,
            ZonedDateTime from,
            Pageable pageable
    );

    Page<CampusEvent> findByStatusAndCategoryAndEventDateAfter(
            EventStatus status,
            EventCategory category,
            ZonedDateTime from,
            Pageable pageable
    );
}

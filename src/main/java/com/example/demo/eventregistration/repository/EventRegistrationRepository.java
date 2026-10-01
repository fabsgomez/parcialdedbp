package com.example.demo.eventregistration.repository;

import com.example.demo.eventregistration.domain.EventRegistration;
import com.example.demo.eventregistration.domain.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    boolean existsByEventIdAndAttendeeId(Long eventId, Long attendeeId);

    Page<EventRegistration> findByAttendeeId(Long attendeeId, Pageable pageable);

    Page<EventRegistration> findByAttendeeIdAndStatus(Long attendeeId, RegistrationStatus status, Pageable pageable);
}

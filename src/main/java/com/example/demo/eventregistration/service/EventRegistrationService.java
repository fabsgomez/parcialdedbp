package com.example.demo.eventregistration.service;

import com.example.demo.campusevent.domain.CampusEvent;
import com.example.demo.campusevent.domain.EventStatus;
import com.example.demo.campusevent.repository.CampusEventRepository;
import com.example.demo.eventregistration.domain.EventRegistration;
import com.example.demo.eventregistration.domain.RegistrationStatus;
import com.example.demo.eventregistration.dto.CreateRegistrationRequest;
import com.example.demo.eventregistration.dto.MyRegistrationResponse;
import com.example.demo.eventregistration.dto.RegistrationResponse;
import com.example.demo.eventregistration.event.RegistrationConfirmedEvent;
import com.example.demo.eventregistration.repository.EventRegistrationRepository;
import com.example.demo.shared.dto.PageResponse;
import com.example.demo.shared.exception.AlreadyRegisteredException;
import com.example.demo.shared.exception.EventNotFoundException;
import com.example.demo.shared.exception.InvalidEventStateException;
import com.example.demo.shared.exception.TicketTypeFullException;
import com.example.demo.shared.exception.TicketTypeNotFoundException;
import com.example.demo.tickettype.domain.TicketType;
import com.example.demo.tickettype.repository.TicketTypeRepository;
import com.example.demo.user.domain.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final CampusEventRepository campusEventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EventRegistrationService(
            EventRegistrationRepository eventRegistrationRepository,
            CampusEventRepository campusEventRepository,
            TicketTypeRepository ticketTypeRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.campusEventRepository = campusEventRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public RegistrationResponse register(Long eventId, CreateRegistrationRequest request, User attendee) {
        CampusEvent event = campusEventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getStatus() != EventStatus.PUBLISHED || event.getEventDate().isBefore(ZonedDateTime.now())) {
            throw new InvalidEventStateException("Event must be published and in the future");
        }

        TicketType ticketType = ticketTypeRepository.findByIdAndEventId(request.ticketTypeId(), eventId)
                .orElseThrow(() -> new TicketTypeNotFoundException(
                        "Ticket type not found or does not belong to the event"
                ));

        if (!ticketType.hasAvailableSlots()) {
            throw new TicketTypeFullException();
        }

        if (eventRegistrationRepository.existsByEventIdAndAttendeeId(eventId, attendee.getId())) {
            throw new AlreadyRegisteredException();
        }

        ticketType.incrementRegistration();

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .ticketType(ticketType)
                .attendee(attendee)
                .registeredAt(ZonedDateTime.now())
                .status(RegistrationStatus.CONFIRMED)
                .build();

        try {
            EventRegistration saved = eventRegistrationRepository.save(registration);
            eventPublisher.publishEvent(new RegistrationConfirmedEvent(
                    saved.getId(),
                    event.getId(),
                    attendee.getId()
            ));
            return new RegistrationResponse(
                    saved.getId(),
                    event.getId(),
                    event.getTitle(),
                    ticketType.getName(),
                    attendee.getUsername(),
                    saved.getStatus()
            );
        } catch (DataIntegrityViolationException ex) {
            throw new AlreadyRegisteredException();
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<MyRegistrationResponse> findMine(User attendee, String status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "registeredAt"));
        Page<EventRegistration> registrations;
        if (status == null || status.equalsIgnoreCase("ALL")) {
            registrations = eventRegistrationRepository.findByAttendeeId(attendee.getId(), pageable);
        } else {
            RegistrationStatus registrationStatus = RegistrationStatus.valueOf(status.toUpperCase());
            registrations = eventRegistrationRepository.findByAttendeeIdAndStatus(
                    attendee.getId(),
                    registrationStatus,
                    pageable
            );
        }

        return PageResponse.of(registrations.map(registration -> new MyRegistrationResponse(
                registration.getId(),
                registration.getEvent().getTitle(),
                registration.getStatus()
        )));
    }
}

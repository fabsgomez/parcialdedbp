package com.example.demo.campusevent.service;

import com.example.demo.campusevent.domain.CampusEvent;
import com.example.demo.campusevent.domain.EventCategory;
import com.example.demo.campusevent.domain.EventStatus;
import com.example.demo.campusevent.dto.CreateEventRequest;
import com.example.demo.campusevent.dto.EventResponse;
import com.example.demo.campusevent.dto.EventSummaryResponse;
import com.example.demo.campusevent.dto.PublishEventResponse;
import com.example.demo.campusevent.repository.CampusEventRepository;
import com.example.demo.shared.dto.PageResponse;
import com.example.demo.shared.exception.EventNotFoundException;
import com.example.demo.shared.exception.ForbiddenEventActionException;
import com.example.demo.shared.exception.InvalidEventStateException;
import com.example.demo.tickettype.domain.TicketTypeStatus;
import com.example.demo.tickettype.repository.TicketTypeRepository;
import com.example.demo.user.domain.Role;
import com.example.demo.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class CampusEventService {

    private final CampusEventRepository campusEventRepository;
    private final TicketTypeRepository ticketTypeRepository;

    public CampusEventService(
            CampusEventRepository campusEventRepository,
            TicketTypeRepository ticketTypeRepository
    ) {
        this.campusEventRepository = campusEventRepository;
        this.ticketTypeRepository = ticketTypeRepository;
    }

    @Transactional
    public EventResponse create(CreateEventRequest request, User organizer) {
        if (organizer.getRole() != Role.ROLE_ORGANIZER && organizer.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenEventActionException("Only organizers or admins can create events");
        }

        CampusEvent event = CampusEvent.builder()
                .organizer(organizer)
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .eventDate(request.eventDate())
                .location(request.location())
                .status(EventStatus.DRAFT)
                .build();

        CampusEvent saved = campusEventRepository.save(event);
        return new EventResponse(
                saved.getId(),
                organizer.getUsername(),
                saved.getTitle(),
                saved.getCategory(),
                saved.getStatus()
        );
    }

    @Transactional
    public PublishEventResponse publish(Long eventId, User actor) {
        CampusEvent event = campusEventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (actor.getRole() != Role.ROLE_ADMIN && !event.getOrganizer().getId().equals(actor.getId())) {
            throw new ForbiddenEventActionException("You cannot publish an event you do not own");
        }

        if (event.getStatus() != EventStatus.DRAFT) {
            throw new InvalidEventStateException("Event can only be published from DRAFT");
        }

        if (!ticketTypeRepository.existsByEventIdAndStatus(eventId, TicketTypeStatus.AVAILABLE)) {
            throw new InvalidEventStateException("Event must have at least one active ticket type");
        }

        event.setStatus(EventStatus.PUBLISHED);
        return new PublishEventResponse(event.getId(), event.getTitle(), event.getStatus());
    }

    @Transactional(readOnly = true)
    public PageResponse<EventSummaryResponse> search(String category, ZonedDateTime from, int page, int size) {
        EventCategory eventCategory = parseCategory(category);
        ZonedDateTime fromDate = from != null ? from : ZonedDateTime.now();
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "eventDate"));

        Page<CampusEvent> events = campusEventRepository.searchPublished(
                EventStatus.PUBLISHED,
                fromDate,
                eventCategory,
                pageable
        );

        return PageResponse.of(events.map(event -> new EventSummaryResponse(
                event.getId(),
                event.getTitle(),
                event.getCategory(),
                event.getEventDate(),
                ticketTypeRepository.sumAvailableSlots(event.getId(), TicketTypeStatus.AVAILABLE)
        )));
    }

    private EventCategory parseCategory(String category) {
        if (category == null || category.isBlank() || category.equalsIgnoreCase("ALL")) {
            return null;
        }
        return EventCategory.valueOf(category.toUpperCase());
    }
}

package com.example.demo.tickettype.service;

import com.example.demo.campusevent.domain.CampusEvent;
import com.example.demo.campusevent.repository.CampusEventRepository;
import com.example.demo.shared.exception.EventNotFoundException;
import com.example.demo.shared.exception.ForbiddenEventActionException;
import com.example.demo.tickettype.domain.TicketType;
import com.example.demo.tickettype.domain.TicketTypeStatus;
import com.example.demo.tickettype.dto.CreateTicketTypeRequest;
import com.example.demo.tickettype.dto.TicketTypeResponse;
import com.example.demo.tickettype.repository.TicketTypeRepository;
import com.example.demo.user.domain.Role;
import com.example.demo.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final CampusEventRepository campusEventRepository;

    public TicketTypeService(
            TicketTypeRepository ticketTypeRepository,
            CampusEventRepository campusEventRepository
    ) {
        this.ticketTypeRepository = ticketTypeRepository;
        this.campusEventRepository = campusEventRepository;
    }

    @Transactional
    public TicketTypeResponse create(Long eventId, CreateTicketTypeRequest request, User actor) {
        CampusEvent event = campusEventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        assertCanManage(event, actor);

        TicketType ticketType = TicketType.builder()
                .event(event)
                .name(request.name())
                .capacity(request.capacity())
                .registeredCount(0)
                .status(TicketTypeStatus.AVAILABLE)
                .build();

        TicketType saved = ticketTypeRepository.save(ticketType);
        return new TicketTypeResponse(
                saved.getId(),
                event.getId(),
                saved.getName(),
                saved.getCapacity(),
                saved.getRegisteredCount(),
                saved.getStatus()
        );
    }

    private void assertCanManage(CampusEvent event, User actor) {
        if (actor.getRole() == Role.ROLE_ADMIN) {
            return;
        }
        if (actor.getRole() != Role.ROLE_ORGANIZER || !event.getOrganizer().getId().equals(actor.getId())) {
            throw new ForbiddenEventActionException("You cannot modify this event");
        }
    }
}

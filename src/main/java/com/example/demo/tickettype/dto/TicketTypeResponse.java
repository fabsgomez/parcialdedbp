package com.example.demo.tickettype.dto;

import com.example.demo.tickettype.domain.TicketTypeStatus;

public record TicketTypeResponse(
        Long id,
        Long eventId,
        String name,
        Integer capacity,
        Integer registeredCount,
        TicketTypeStatus status
) {
}

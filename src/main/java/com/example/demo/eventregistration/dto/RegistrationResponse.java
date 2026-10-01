package com.example.demo.eventregistration.dto;

import com.example.demo.eventregistration.domain.RegistrationStatus;

public record RegistrationResponse(
        Long id,
        Long eventId,
        String eventTitle,
        String ticketType,
        String attendeeUsername,
        RegistrationStatus status
) {
}

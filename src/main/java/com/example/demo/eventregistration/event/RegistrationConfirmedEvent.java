package com.example.demo.eventregistration.event;

public record RegistrationConfirmedEvent(
        Long registrationId,
        Long eventId,
        Long attendeeId
) {
}

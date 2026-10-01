package com.example.demo.eventregistration.dto;

import com.example.demo.eventregistration.domain.RegistrationStatus;

public record MyRegistrationResponse(
        Long id,
        String eventTitle,
        RegistrationStatus status
) {
}

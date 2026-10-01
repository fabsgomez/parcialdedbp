package com.example.demo.eventregistration.dto;

import jakarta.validation.constraints.NotNull;

public record CreateRegistrationRequest(
        @NotNull Long ticketTypeId
) {
}

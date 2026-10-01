package com.example.demo.tickettype.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketTypeRequest(
        @NotBlank String name,
        @NotNull @Min(1) Integer capacity
) {
}

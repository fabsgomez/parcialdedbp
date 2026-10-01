package com.example.demo.campusevent.dto;

import com.example.demo.campusevent.domain.EventCategory;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;

public record CreateEventRequest(
        @NotBlank @Size(min = 5, max = 120) String title,
        @Size(max = 500) String description,
        @NotNull EventCategory category,
        @NotNull @Future ZonedDateTime eventDate,
        @NotBlank String location
) {
}

package com.example.demo.campusevent.dto;

import com.example.demo.campusevent.domain.EventCategory;

import java.time.ZonedDateTime;

public record EventSummaryResponse(
        Long id,
        String title,
        EventCategory category,
        ZonedDateTime eventDate,
        long availableSlots
) {
}

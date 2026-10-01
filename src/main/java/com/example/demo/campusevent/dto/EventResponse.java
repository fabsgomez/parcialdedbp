package com.example.demo.campusevent.dto;

import com.example.demo.campusevent.domain.EventCategory;
import com.example.demo.campusevent.domain.EventStatus;

public record EventResponse(
        Long id,
        String organizerUsername,
        String title,
        EventCategory category,
        EventStatus status
) {
}

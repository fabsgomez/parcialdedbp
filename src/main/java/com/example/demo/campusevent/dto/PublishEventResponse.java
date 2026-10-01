package com.example.demo.campusevent.dto;

import com.example.demo.campusevent.domain.EventCategory;
import com.example.demo.campusevent.domain.EventStatus;

public record PublishEventResponse(
        Long id,
        String title,
        EventStatus status
) {
}

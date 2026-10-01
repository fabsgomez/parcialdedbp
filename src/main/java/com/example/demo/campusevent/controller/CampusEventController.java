package com.example.demo.campusevent.controller;

import com.example.demo.campusevent.dto.CreateEventRequest;
import com.example.demo.campusevent.dto.EventResponse;
import com.example.demo.campusevent.dto.EventSummaryResponse;
import com.example.demo.campusevent.dto.PublishEventResponse;
import com.example.demo.campusevent.service.CampusEventService;
import com.example.demo.shared.dto.PageResponse;
import com.example.demo.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/events")
public class CampusEventController {

    private final CampusEventService campusEventService;
    private final UserService userService;

    public CampusEventController(CampusEventService campusEventService, UserService userService) {
        this.campusEventService = campusEventService;
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@Valid @RequestBody CreateEventRequest request) {
        return campusEventService.create(request, userService.getAuthenticatedUser());
    }

    @PatchMapping("/{eventId}/publish")
    public PublishEventResponse publish(@PathVariable Long eventId) {
        return campusEventService.publish(eventId, userService.getAuthenticatedUser());
    }

    @GetMapping
    public PageResponse<EventSummaryResponse> search(
            @RequestParam(defaultValue = "ALL") String category,
            @RequestParam(required = false) ZonedDateTime from,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return campusEventService.search(category, from, page, size);
    }
}

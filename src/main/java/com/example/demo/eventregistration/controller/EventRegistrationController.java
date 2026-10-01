package com.example.demo.eventregistration.controller;

import com.example.demo.eventregistration.dto.CreateRegistrationRequest;
import com.example.demo.eventregistration.dto.MyRegistrationResponse;
import com.example.demo.eventregistration.dto.RegistrationResponse;
import com.example.demo.eventregistration.service.EventRegistrationService;
import com.example.demo.shared.dto.PageResponse;
import com.example.demo.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;
    private final UserService userService;

    public EventRegistrationController(
            EventRegistrationService eventRegistrationService,
            UserService userService
    ) {
        this.eventRegistrationService = eventRegistrationService;
        this.userService = userService;
    }

    @PostMapping("/events/{eventId}/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse register(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateRegistrationRequest request
    ) {
        return eventRegistrationService.register(eventId, request, userService.getAuthenticatedUser());
    }

    @GetMapping("/my-event-registrations")
    public PageResponse<MyRegistrationResponse> myRegistrations(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return eventRegistrationService.findMine(userService.getAuthenticatedUser(), status, page, size);
    }
}

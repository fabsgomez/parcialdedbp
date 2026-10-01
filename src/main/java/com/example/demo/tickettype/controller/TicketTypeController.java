package com.example.demo.tickettype.controller;

import com.example.demo.tickettype.dto.CreateTicketTypeRequest;
import com.example.demo.tickettype.dto.TicketTypeResponse;
import com.example.demo.tickettype.service.TicketTypeService;
import com.example.demo.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;
    private final UserService userService;

    public TicketTypeController(TicketTypeService ticketTypeService, UserService userService) {
        this.ticketTypeService = ticketTypeService;
        this.userService = userService;
    }

    @PostMapping("/events/{eventId}/ticket-types")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketTypeResponse create(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateTicketTypeRequest request
    ) {
        return ticketTypeService.create(eventId, request, userService.getAuthenticatedUser());
    }
}

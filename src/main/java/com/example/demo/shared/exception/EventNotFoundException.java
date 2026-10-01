package com.example.demo.shared.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(Long eventId) {
        super("Event not found: " + eventId);
    }
}

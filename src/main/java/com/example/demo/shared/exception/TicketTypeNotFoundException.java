package com.example.demo.shared.exception;

public class TicketTypeNotFoundException extends RuntimeException {

    public TicketTypeNotFoundException(String message) {
        super(message);
    }
}

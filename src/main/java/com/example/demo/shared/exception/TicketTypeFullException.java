package com.example.demo.shared.exception;

public class TicketTypeFullException extends RuntimeException {

    public TicketTypeFullException() {
        super("Ticket type has no remaining capacity");
    }
}

package com.example.demo.shared.exception;

public class AlreadyRegisteredException extends RuntimeException {

    public AlreadyRegisteredException() {
        super("User is already registered for this event");
    }
}

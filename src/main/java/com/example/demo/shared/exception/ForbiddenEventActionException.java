package com.example.demo.shared.exception;

public class ForbiddenEventActionException extends RuntimeException {

    public ForbiddenEventActionException(String message) {
        super(message);
    }
}

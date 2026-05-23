package com.microservice.uploadservice.application.exceptions;

public class UnauthorizedActionException extends BusinessException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}

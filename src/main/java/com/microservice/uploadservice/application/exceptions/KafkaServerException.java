package com.microservice.uploadservice.application.exceptions;

public class KafkaServerException extends BusinessException {
    public KafkaServerException(String message) {
        super(message);
    }
}

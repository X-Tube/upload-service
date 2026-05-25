package com.microservice.uploadservice.application.exceptions;

public class S3ServerException extends BusinessException {
    public S3ServerException(String message) {
        super(message);
    }
}

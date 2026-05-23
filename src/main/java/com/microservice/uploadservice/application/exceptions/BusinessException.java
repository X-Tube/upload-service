package com.microservice.uploadservice.application.exceptions;

public abstract class BusinessException extends RuntimeException{

    protected BusinessException(String message){
        super(message);
    }
}

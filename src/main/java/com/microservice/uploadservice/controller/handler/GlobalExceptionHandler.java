package com.microservice.uploadservice.controller.handler;

import com.microservice.uploadservice.application.exceptions.UnauthorizedActionException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<StandardError> handleUnauthorizedActionException(UnauthorizedActionException e, HttpServletRequest request) {
        var response = StandardError.builder()
                .error(e.getMessage())
                .timestamp(LocalDate.now())
                .path(request.getRequestURI())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidationErrors(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce("", (acc, error) -> acc + error + "; ");

        var response = StandardError.builder()
                .error("Error validating: " + errorMessage)
                .timestamp(LocalDate.now())
                .path(request.getRequestURI())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
}

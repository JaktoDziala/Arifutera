package com.example.Arifutera.exceptions.handler;

import com.example.Arifutera.exceptions.DataProcessingException;
import com.example.Arifutera.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(messageBuilder(e.getMessage(),
                HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataProcessingException.class)
    public ResponseEntity<?> handleDataProcessingException(DataProcessingException e) {
        return new ResponseEntity<>(messageBuilder(e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, String> messageBuilder(String message, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("status", status.name());
        response.put("message", message);
        return response;
    }
}

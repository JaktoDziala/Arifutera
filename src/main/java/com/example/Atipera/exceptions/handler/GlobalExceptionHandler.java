package com.example.Atipera.exceptions.handler;

import com.example.Atipera.exceptions.InvalidRequestDataException;
import com.example.Atipera.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestDataException.class)
    public ResponseEntity<?> handleMissingRequestDataException(InvalidRequestDataException e) {
        return new ResponseEntity<>(messageBuilder(e.getMessage() + " Please provide missing data.",
                HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(messageBuilder(e.getMessage(),
                HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    private Map<String, String> messageBuilder(String message, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("status", status.name());
        response.put("message", message);
        return response;
    }
}

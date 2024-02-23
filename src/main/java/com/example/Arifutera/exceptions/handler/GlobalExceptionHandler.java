package com.example.Arifutera.exceptions.handler;

import com.example.Arifutera.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(messageBuilder(e.getMessage(),
                NOT_FOUND), NOT_FOUND);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException ex) {
        return switch ((HttpStatus) ex.getStatusCode()) {
            case NOT_FOUND -> new ResponseEntity<>(messageBuilder("User of given username could not be found.",
                    ex.getStatusCode()), NOT_FOUND);
            case BAD_REQUEST -> new ResponseEntity<>(messageBuilder(ex.getMessage(),
                    ex.getStatusCode()), BAD_REQUEST);
            default -> new ResponseEntity<>(messageBuilder(ex.getMessage(),
                    ex.getStatusCode()), INTERNAL_SERVER_ERROR);
        };
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<?> handleWebClientResponseException(WebClientResponseException ex) {
        return switch ((HttpStatus) ex.getStatusCode()) {
            case NOT_FOUND -> new ResponseEntity<>(messageBuilder("User of given username could not be found.",
                    ex.getStatusCode()), NOT_FOUND);
            case BAD_REQUEST -> new ResponseEntity<>(messageBuilder(ex.getMessage(),
                    ex.getStatusCode()), BAD_REQUEST);
            default -> new ResponseEntity<>(messageBuilder(ex.getMessage(),
                    ex.getStatusCode()), INTERNAL_SERVER_ERROR);
        };
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<?> handleWebClientRequestException(WebClientRequestException ex) {
        return new ResponseEntity<>(messageBuilder(ex.getMessage(),
            INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    private Map<String, String> messageBuilder(String message, HttpStatusCode status) {
        Map<String, String> response = new HashMap<>();
        response.put("status", status.toString());
        response.put("message", message);
        return response;
    }
}

package com.qualitymanagementsystem.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** Global exception handler for the whole application */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Handle case when user is not found (404) */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UsernameNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Handle validation errors (like @NotBlank, @Size, @Email) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return buildResponse(HttpStatus.BAD_REQUEST, errors.toString());
    }

    /** Handle unexpected runtime exceptions (500) */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    /** Handle all other exceptions not covered above */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<Object> handleInvalidOtp(InvalidOtpException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    /** Helper method to build consistent JSON responses */
    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, status);
    }
}

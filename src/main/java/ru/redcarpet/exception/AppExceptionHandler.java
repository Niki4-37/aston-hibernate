package ru.redcarpet.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(exception = {
        IllegalStateException.class,
        IllegalArgumentException.class
    })
    public ResponseEntity<ErrorDto> handleBadRequest(Exception e) {
        var errorDto = new ErrorDto(
            "Bad request",
            e.getMessage(), 
            LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(exception = org.springframework.dao.DataIntegrityViolationException.class) 
    public ResponseEntity<ErrorDto> handleDatabase(Exception e) {
        var errorDto = new ErrorDto(
            "Can't create user in database",
            e.getMessage(),
            LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
    }

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFound(Exception e) {
        var errorDto = new ErrorDto(
            "Can't find such user",
            e.getMessage(),
            LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }   
}

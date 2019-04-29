package com.zica.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handles these specific types of exceptions (in each method below)
 * when they happen in a controller class
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(CryptographyException.class)
    public ResponseEntity handleCryptographyException(CryptographyException e) {
        var msg = new ErrorMessage("An application error occurred: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
    }

}

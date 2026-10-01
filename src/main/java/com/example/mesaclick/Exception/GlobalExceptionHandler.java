package com.example.mesaclick.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.mesaclick.Exception.CredencialesInvalidasException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CorreoYaRegistradoException.class)
    public ResponseEntity<?> manejarCorreoDuplicado(
            CorreoYaRegistradoException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", ex.getMessage()
                ));
    }

@ExceptionHandler(CredencialesInvalidasException.class)
public ResponseEntity<?> manejarCredencialesInvalidas(
        CredencialesInvalidasException ex) {

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(Map.of(
                    "error", ex.getMessage()
            ));
}
}
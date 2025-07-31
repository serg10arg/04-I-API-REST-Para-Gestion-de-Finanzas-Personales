package com.example.finanzaspersonales.handler;

import com.example.finanzaspersonales.dto.error.ErrorResponseDTO;
import com.example.finanzaspersonales.exception.AccesoDenegadoException;
import com.example.finanzaspersonales.exception.RecursoNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleRecursoNoEncontradoException(RecursoNoEncontradoException ex, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccesoDenegadoException(AccesoDenegadoException ex, HttpServletRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Puedes añadir más manejadores para otras excepciones, como las de validación.
    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // ...
}
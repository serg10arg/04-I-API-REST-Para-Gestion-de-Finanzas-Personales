package com.example.finanzaspersonales.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un recurso solicitado no se encuentra.
 * Se mapea a un código de estado HTTP 404 (Not Found).
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

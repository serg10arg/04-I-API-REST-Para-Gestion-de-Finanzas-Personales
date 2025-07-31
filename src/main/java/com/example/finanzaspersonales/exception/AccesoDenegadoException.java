package com.example.finanzaspersonales.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un usuario intenta acceder o modificar un recurso
 * al que no tiene permiso. Se mapea a un código de estado HTTP 403 (Forbidden).
 * Esto es crucial para asegurar la privacidad de la información del usuario.
 */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}


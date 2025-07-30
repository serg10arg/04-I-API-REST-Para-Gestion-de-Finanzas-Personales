package com.example.finanzaspersonales.dto.autenticacion;

import lombok.Value;

/**
 * DTO para encapsular el token JWT devuelto tras un inicio de sesión exitoso.
 */
@Value
public class TokenDTO {
    String token;
}

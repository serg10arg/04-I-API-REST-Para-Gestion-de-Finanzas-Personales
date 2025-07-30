package com.example.finanzaspersonales.dto.autenticacion;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para las credenciales de inicio de sesión de un usuario.
 */
@Data
public class UsuarioLoginDTO {

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String nombreUsuario;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String contrasena;
}


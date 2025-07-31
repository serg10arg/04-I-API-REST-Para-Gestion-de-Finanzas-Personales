package com.example.finanzaspersonales.controladores;

import com.example.finanzaspersonales.dto.autenticacion.TokenDTO;
import com.example.finanzaspersonales.dto.autenticacion.UsuarioLoginDTO;
import com.example.finanzaspersonales.dto.autenticacion.UsuarioRegistroDTO;
import com.example.finanzaspersonales.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la autenticación y registro de usuarios.
 * Expone los endpoints /register y /login.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para registro e inicio de sesión de usuarios.")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * @param usuarioRegistroDTO DTO con la información de registro del usuario.
     * @return ResponseEntity con el usuario registrado o un mensaje de error.
     */
    @Operation(summary = "Registra un nuevo usuario",
            description = "Crea una nueva cuenta de usuario en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o nombre de usuario ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<UsuarioRegistroDTO> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO usuarioRegistroDTO) {
        usuarioService.registrarUsuario(usuarioRegistroDTO);
        return new ResponseEntity<>(usuarioRegistroDTO, HttpStatus.CREATED);
    }

    /**
     * Endpoint para iniciar sesión.
     * Si las credenciales son válidas, devuelve un token JWT.
     * @param usuarioLoginDTO DTO con las credenciales de inicio de sesión.
     * @return ResponseEntity con el TokenDTO (JWT) o un error de autenticación.
     */
    @Operation(summary = "Inicia sesión y obtiene un token JWT",
            description = "Autentica al usuario y devuelve un token JWT para acceder a los recursos protegidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, token JWT devuelto"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody UsuarioLoginDTO usuarioLoginDTO) {
        TokenDTO token = usuarioService.autenticarUsuario(usuarioLoginDTO);
        return ResponseEntity.ok(token);
    }
}

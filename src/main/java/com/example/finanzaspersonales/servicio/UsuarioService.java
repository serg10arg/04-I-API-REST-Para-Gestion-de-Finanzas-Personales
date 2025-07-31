package com.example.finanzaspersonales.servicio;

import com.example.finanzaspersonales.dto.autenticacion.TokenDTO;
import com.example.finanzaspersonales.dto.autenticacion.UsuarioLoginDTO;
import com.example.finanzaspersonales.dto.autenticacion.UsuarioRegistroDTO;
import com.example.finanzaspersonales.exception.RecursoNoEncontradoException; // Importar excepción
import com.example.finanzaspersonales.mapper.UsuarioMapper;
import com.example.finanzaspersonales.modelo.Rol; // Importar Rol
import com.example.finanzaspersonales.modelo.Usuario;
import com.example.finanzaspersonales.repositorio.RolRepository; // Importar RolRepository
import com.example.finanzaspersonales.repositorio.UsuarioRepository;
import com.example.finanzaspersonales.seguridad.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set; // Importar Set

/**
 * Servicio para la gestión de usuarios, incluyendo registro y autenticación.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository; // 1. Inyectar el repositorio de roles
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, // 2. Añadir al constructor
                          PasswordEncoder passwordEncoder, JwtService jwtService,
                          AuthenticationManager authenticationManager, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.usuarioMapper = usuarioMapper;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Codifica la contraseña y asigna el rol de usuario por defecto.
     * @param usuarioRegistroDTO DTO con la información del nuevo usuario.
     * @return El usuario registrado (sin la contraseña en texto plano).
     * @throws IllegalArgumentException si el nombre de usuario ya existe.
     * @throws RecursoNoEncontradoException si el rol por defecto 'ROLE_USER' no se encuentra en la base de datos.
     */
    @Transactional
    public UsuarioRegistroDTO registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO) {
        if (usuarioRepository.existsByNombreUsuario(usuarioRegistroDTO.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }

        // 3. Buscar la entidad Rol en la base de datos
        Rol rolUsuario = rolRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new RecursoNoEncontradoException("Error de configuración: El rol 'ROLE_USER' no existe."));

        Usuario nuevoUsuario = usuarioMapper.aUsuario(usuarioRegistroDTO);
        nuevoUsuario.setContrasena(passwordEncoder.encode(usuarioRegistroDTO.getContrasena()));

        // 4. Asignar el Set que contiene la entidad Rol
        nuevoUsuario.setRoles(Set.of(rolUsuario));

        usuarioRepository.save(nuevoUsuario);
        return usuarioRegistroDTO;
    }

    /**
     * Autentica a un usuario y genera un token JWT.
     * @param usuarioLoginDTO DTO con las credenciales del usuario.
     * @return Un TokenDTO con el JWT.
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son inválidas.
     */
    public TokenDTO autenticarUsuario(UsuarioLoginDTO usuarioLoginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioLoginDTO.getNombreUsuario(), usuarioLoginDTO.getContrasena())
        );

        String jwt = jwtService.generarToken(authentication.getName());

        return new TokenDTO(jwt);
    }
}
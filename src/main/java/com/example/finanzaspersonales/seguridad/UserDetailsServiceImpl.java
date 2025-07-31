package com.example.finanzaspersonales.seguridad;

import com.example.finanzaspersonales.modelo.Usuario;
import com.example.finanzaspersonales.repositorio.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de UserDetailsService para cargar los detalles del usuario
 * desde la base de datos para la autenticación de Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga los detalles de un usuario por su nombre de usuario.
     * @param username El nombre de usuario del usuario a cargar.
     * @return Un objeto UserDetails que representa al usuario.
     * @throws UsernameNotFoundException si el usuario no se encuentra.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());

        return User.builder()
                .username(usuario.getNombreUsuario())
                .password(usuario.getContrasena())
                .authorities(authorities) // se asignan las autoridades correctamente
                .build();
    }
}


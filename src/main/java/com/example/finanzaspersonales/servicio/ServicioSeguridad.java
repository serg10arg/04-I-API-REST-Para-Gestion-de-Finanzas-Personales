package com.example.finanzaspersonales.servicio;

import com.example.finanzaspersonales.exception.AccesoDenegadoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Servicio para interactuar con el contexto de seguridad de Spring.
 * Proporciona información sobre el usuario actualmente autenticado.
 * Al ser un bean de Spring, es fácilmente inyectable y mockeable en tests.
 */
@Service
public class ServicioSeguridad {

    /**
     * Obtiene el nombre de usuario del usuario actualmente autenticado.
     * @return El nombre de usuario autenticado.
     * @throws AccesoDenegadoException Si no hay un usuario autenticado en el contexto.
     */
    public String obtenerNombreUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccesoDenegadoException("No hay un usuario autenticado o la sesión ha expirado.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}
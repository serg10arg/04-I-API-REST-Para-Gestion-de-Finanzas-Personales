package com.example.finanzaspersonales.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro personalizado para la autenticación de JWT.
 * Se ejecuta una vez por cada solicitud HTTP para validar el token JWT.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String nombreUsuario;

        // Si el encabezado de autorización no existe o no empieza con "Bearer ", se ignora este filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrae el token JWT del encabezado
        jwt = authHeader.substring(7);
        // Extrae el nombre de usuario del token
        nombreUsuario = jwtService.extraerNombreUsuario(jwt);

        // Si el nombre de usuario no es nulo y no hay una autenticación ya establecida en el contexto de seguridad
        if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(nombreUsuario);

            // Valida el token JWT
            if (jwtService.esTokenValido(jwt, userDetails.getUsername())) {
                // Crea un objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // Establece los detalles de la solicitud
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Establece el objeto de autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}


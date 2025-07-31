package com.example.finanzaspersonales.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Clase de configuración para Spring Security.
 * Habilita la seguridad web y la seguridad a nivel de método (pre/post-autorización).
 */
@Configuration
@EnableWebSecurity // Habilita la integración de Spring Security con Spring MVC
@EnableMethodSecurity // Permite la seguridad a nivel de método (ej. @PreAuthorize)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;
    // Ya no es necesario inyectar el PasswordEncoder, lo proveeremos como un bean.

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Define la cadena de filtros de seguridad HTTP.
     * Configura qué endpoints están protegidos y cuáles son públicos.
     * @param http Objeto HttpSecurity para configurar la seguridad.
     * @return La cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs REST sin sesión
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints públicos para autenticación y registro
                        .requestMatchers("/api/auth/**").permitAll()
                        // Acceso a Swagger/OpenAPI UI y documentación
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        // Consola H2 (solo para desarrollo, ¡no usar en producción!)
                        .requestMatchers("/h2-console/**").permitAll()
                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No usa sesiones HTTP (estilo RESTful)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Añade el filtro JWT antes del filtro de usuario/contraseña

        // Necesario para H2 console si CSRF está deshabilitado
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    /**
     * Define el proveedor de autenticación.
     * Utiliza DaoAuthenticationProvider para autenticación basada en usuario y contraseña
     * con el servicio UserDetailsService y el PasswordEncoder.
     * @return El AuthenticationProvider configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder()); // Configura el codificador de contraseñas
        return authProvider;
    }

    /**
     * Expone el AuthenticationManager de Spring Security como un bean.
     * Es necesario para poder inyectarlo en el UsuarioService para el proceso de login.
     * @param config La configuración de autenticación de Spring.
     * @return El AuthenticationManager.
     * @throws Exception Si no se puede obtener el AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el bean para el codificador de contraseñas.
     * Se utiliza BCrypt, que es el estándar actual y recomendado por su fortaleza.
     * @return una instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
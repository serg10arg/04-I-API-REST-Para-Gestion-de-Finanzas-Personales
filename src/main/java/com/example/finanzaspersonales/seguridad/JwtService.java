package com.example.finanzaspersonales.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la generación y validación de JSON Web Tokens (JWT).
 * Utiliza la librería JJWT.
 */
@Service
public class JwtService {

    // Se carga la clave secreta desde application.properties
    @Value("${jwt.secret}")
    private String CLAVE_SECRETA;

    // Se carga el tiempo de expiración desde application.properties
    @Value("${jwt.expiration}")
    private long TIEMPO_EXPIRACION;

    /**
     * Genera un token JWT para un nombre de usuario dado.
     * @param nombreUsuario El nombre de usuario.
     * @return El token JWT generado.
     */
    public String generarToken(String nombreUsuario) {
        Map<String, Object> claims = new HashMap<>();
        return crearToken(claims, nombreUsuario);
    }

    /**
     * Crea el token JWT con claims, sujeto y fecha de expiración.
     * @param claims Claims adicionales.
     * @param subject El sujeto (nombre de usuario).
     * @return El token JWT.
     */
    private String crearToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION)) // Token expira después de TIEMPO_EXPIRACION ms
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma el token con la clave secreta
                .compact();
    }

    /**
     * Obtiene la clave de firma decodificada.
     * @return La clave de firma.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(CLAVE_SECRETA);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el nombre de usuario del token JWT.
     * @param token El token JWT.
     * @return El nombre de usuario.
     */
    public String extraerNombreUsuario(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim específico del token JWT.
     * @param token El token JWT.
     * @param claimsResolver Función para resolver el claim.
     * @param <T> Tipo del claim.
     * @return El valor del claim.
     */
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token JWT.
     * @param token El token JWT.
     * @return Los claims del token.
     */
    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Valida si un token JWT es válido para un usuario y no ha expirado.
     * @param token El token JWT.
     * @param nombreUsuario El nombre de usuario.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean esTokenValido(String token, String nombreUsuario) {
        final String usernameExtraido = extraerNombreUsuario(token);
        return (usernameExtraido.equals(nombreUsuario) && !esTokenExpirado(token));
    }

    /**
     * Verifica si el token JWT ha expirado.
     * @param token El token JWT.
     * @return true si el token ha expirado, false en caso contrario.
     */
    private boolean esTokenExpirado(String token) {
        return extraerFechaExpiracion(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     * @param token El token JWT.
     * @return La fecha de expiración.
     */
    private Date extraerFechaExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }
}


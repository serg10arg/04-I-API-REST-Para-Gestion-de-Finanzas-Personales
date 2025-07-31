package com.example.finanzaspersonales.repositorio;

import com.example.finanzaspersonales.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Permite realizar operaciones CRUD básicas y búsquedas personalizadas.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     * @param nombreUsuario El nombre de usuario.
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    /**
     * Verifica si existe un usuario con un nombre de usuario dado.
     * @param nombreUsuario El nombre de usuario a verificar.
     * @return true si existe un usuario con ese nombre de usuario, false en caso contrario.
     */
    boolean existsByNombreUsuario(String nombreUsuario);
}

package com.example.finanzaspersonales.repositorio;

import com.example.finanzaspersonales.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Rol.
 * Permite realizar operaciones CRUD sobre los roles del sistema.
 */
@Repository // 1. Anotación clave: Declara esta interfaz como un bean de repositorio de Spring.
public interface RolRepository extends JpaRepository<Rol, Long> { // 2. Extiende JpaRepository para obtener los métodos CRUD.

    /**
     * Encuentra un rol por su nombre.
     * Este método es crucial para el servicio de registro de usuarios.
     * @param nombre El nombre del rol (ej. "ROLE_USER").
     * @return Un Optional que contiene el rol si se encuentra, o vacío si no.
     */
    Optional<Rol> findByNombre(String nombre); // 3. Método derivado que Spring implementará automáticamente.
}
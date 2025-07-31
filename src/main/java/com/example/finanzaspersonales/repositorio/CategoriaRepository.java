package com.example.finanzaspersonales.repositorio;

import com.example.finanzaspersonales.modelo.Categoria;
import com.example.finanzaspersonales.modelo.Usuario;
import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Categoria.
 * Permite realizar operaciones CRUD y buscar categorías asociadas a un usuario.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Encuentra todas las categorías asociadas a un usuario específico.
     * @param usuario El usuario propietario de las categorías.
     * @return Una lista de categorías.
     */
    List<Categoria> findByUsuario(Usuario usuario);

    /**
     * Encuentra una categoría por su ID y el usuario al que pertenece.
     * Esto es crucial para asegurar que un usuario solo pueda acceder a sus propias categorías.
     * @param id El ID de la categoría.
     * @param usuario El usuario propietario de la categoría.
     * @return Un Optional que contiene la categoría si se encuentra y pertenece al usuario, o vacío si no.
     */
    Optional<Categoria> findByIdAndUsuario(Long id, Usuario usuario);

    /**
     * Verifica si una categoría con un nombre y tipo específicos ya existe para un usuario dado.
     * @param nombre El nombre de la categoría.
     * @param tipo El tipo de la categoría (INGRESO/EGRESO).
     * @param usuario El usuario al que pertenece la categoría.
     * @return true si la categoría existe para ese usuario, false en caso contrario.
     */
    boolean existsByNombreAndTipoAndUsuario(String nombre, TipoTransaccion tipo, Usuario usuario);
}


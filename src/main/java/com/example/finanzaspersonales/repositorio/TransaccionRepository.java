package com.example.finanzaspersonales.repositorio;


import com.example.finanzaspersonales.modelo.Transaccion;
import com.example.finanzaspersonales.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// ... (resto de la clase)
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    @Query("SELECT t FROM Transaccion t WHERE t.categoria.usuario = :usuario")
    List<Transaccion> findAllByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT t FROM Transaccion t WHERE t.id = :id AND t.categoria.usuario = :usuario")
    Optional<Transaccion> findByIdAndUsuario(@Param("id") Long id, @Param("usuario") Usuario usuario);

    @Query("SELECT t FROM Transaccion t WHERE t.categoria.usuario = :usuario AND t.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Transaccion> findAllByUsuarioAndFechaBetween(
            @Param("usuario") Usuario usuario,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
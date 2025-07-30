package com.example.finanzaspersonales.modelo;

import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.*;


/**
 * Entidad que representa una categoría para transacciones (ingresos o gastos).
 * Cada categoría está asociada a un usuario específico para garantizar su privacidad.
 */

@Getter
@Setter
@EqualsAndHashCode (of = "id")
@ToString(exclude = "usuario")
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo; // Puede ser INGRESO o EGRESO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // La categoría pertenece a un usuario
}


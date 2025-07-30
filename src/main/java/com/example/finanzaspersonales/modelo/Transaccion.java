package com.example.finanzaspersonales.modelo;

import jakarta.persistence.*;
import lombok.*;
import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa una transacción financiera (ingreso o egreso).
 * Cada transacción está asociada a un usuario y una categoría específica.
 */
@Entity
@Table(name = "transacciones")
@Getter
@Setter
@ToString (exclude = {"categoria"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo; // INGRESO o EGRESO

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

}


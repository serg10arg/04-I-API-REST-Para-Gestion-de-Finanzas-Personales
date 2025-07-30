package com.example.finanzaspersonales.dto.operaciones;

import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para la transferencia de datos de transacciones.
 * Abstrae la entidad Transaccion, ocultando la relación con el usuario directamente.
 */
@Data
public class TransaccionDTO {
    private Long id;

    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.01", message = "El monto debe ser positivo")
    private BigDecimal monto;

    @NotNull(message = "El tipo de transacción no puede ser nulo (INGRESO/EGRESO)")
    private TipoTransaccion tipo;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "La fecha no puede ser nula")
    private LocalDate fecha;

    @NotNull(message = "La categoría no puede ser nula")
    private Long categoriaId;
}


package com.example.finanzaspersonales.dto.operaciones;

import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para la transferencia de datos de categorías.
 * Abstrae la entidad Categoria, ocultando la relación con el usuario directamente.
 */
@Data
public class CategoriaDTO {
    private Long id;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    private String nombre;

    @NotNull(message = "El tipo de transacción no puede ser nulo (INGRESO/EGRESO)")
    private TipoTransaccion tipo;
}

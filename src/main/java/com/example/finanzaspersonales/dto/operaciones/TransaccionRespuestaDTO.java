package com.example.finanzaspersonales.dto.operaciones;

import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para una Transacción.
 * Este objeto está enriquecido con la información completa de la categoría,
 * optimizando la comunicación con el cliente de la API.
 */
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionRespuestaDTO {

    private Long id;
    private BigDecimal monto;
    private TipoTransaccion tipo;
    private String descripcion;
    private LocalDate fecha;
    private CategoriaDTO categoria; // Incluye el DTO completo de la categoría

}
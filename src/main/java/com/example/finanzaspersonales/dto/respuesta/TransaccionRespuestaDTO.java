package com.example.finanzaspersonales.dto.respuesta;

import com.example.finanzaspersonales.dto.operaciones.CategoriaDTO;
import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para devolver información detallada de una transacción, incluyendo los detalles de la categoría.
 * Utilizado como respuesta en los endpoints GET para enriquecer la información devuelta al cliente
 * y evitar que este tenga que realizar llamadas adicionales.
 */
@Data
public class TransaccionRespuestaDTO {
    private Long id;
    private BigDecimal monto;
    private TipoTransaccion tipo;
    private String descripcion;
    private LocalDate fecha;

    // En lugar de solo el ID, devolvemos el objeto CategoriaDTO completo.
    // Esto evita que el cliente tenga que hacer una llamada adicional para obtener los detalles de la categoría.
    private CategoriaDTO categoria;
}
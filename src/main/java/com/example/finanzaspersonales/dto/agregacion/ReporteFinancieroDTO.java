package com.example.finanzaspersonales.dto.agregacion;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO que representa un resumen financiero para un período determinado.
 * Incluye el total de ingresos, total de egresos y un desglose de gastos por categoría.
 * Es inmutable para garantizar la integridad de los datos una vez creado
 */
@Value // Anotacion de lombok que genera una clase inmutable (final fields, getters, constructor, etc)
public class ReporteFinancieroDTO {
     BigDecimal totalIngresos;
     BigDecimal totalEgresos;
     BigDecimal balanceNeto;
    // Mapa donde la clave es el nombre de la categoría y el valor es el total gastado en ella.
     Map<String, BigDecimal> gastosPorCategoria;
}

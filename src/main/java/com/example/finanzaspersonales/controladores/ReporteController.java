package com.example.finanzaspersonales.controladores;


import com.example.finanzaspersonales.dto.agregacion.ReporteFinancieroDTO;
import com.example.finanzaspersonales.servicio.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controlador REST para la generación de reportes financieros.
 * Expone un endpoint para obtener un resumen de finanzas para un período de tiempo.
 */
@RestController
@RequestMapping("/api/reportes")
@SecurityRequirement(name = "bearerAuth") // Indica que este controlador requiere autenticación JWT
@Tag(name = "Generación de Reportes", description = "Endpoints para la generación de resúmenes financieros.")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * Genera un reporte financiero para el usuario autenticado para un período de tiempo dado.
     * URL: GET /api/reportes/financiero?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD
     * @param fechaInicio Fecha de inicio del período (formato YYYY-MM-DD).
     * @param fechaFin Fecha de fin del período (formato YYYY-MM-DD).
     * @return ResponseEntity con el ReporteFinancieroDTO.
     */
    @Operation(summary = "Genera un reporte financiero",
            description = "Devuelve un resumen de las finanzas del usuario autenticado para un período de tiempo determinado, agrupando los gastos por categoría.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Fechas inválidas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/financiero")
    public ResponseEntity<ReporteFinancieroDTO> generarReporteFinanciero(
            @Parameter(description = "Fecha de inicio del período (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin del período (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        ReporteFinancieroDTO reporte = reporteService.generarReporteFinanciero(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}


package com.example.finanzaspersonales.servicio;

import com.example.finanzaspersonales.dto.agregacion.ReporteFinancieroDTO;
import com.example.finanzaspersonales.exception.RecursoNoEncontradoException;
import com.example.finanzaspersonales.modelo.Categoria;
import com.example.finanzaspersonales.modelo.Rol;
import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import com.example.finanzaspersonales.modelo.Transaccion;
import com.example.finanzaspersonales.modelo.Usuario;
import com.example.finanzaspersonales.repositorio.TransaccionRepository;
import com.example.finanzaspersonales.repositorio.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para el ReporteService.
 * Prueba la lógica de negocio de los reportes: mockea el repositorio de transacciones
 * con datos de prueba y verifica que el servicio calcula los totales y las agrupaciones
 * por categoría correctamente. Prueba también los casos de borde (ej. un mes sin transacciones).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para ReporteService")
class ReporteServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ServicioSeguridad servicioSeguridad; // 1. Mockear el servicio, no la utilidad estática

    @InjectMocks
    private ReporteService reporteService;

    private Usuario usuarioPrueba;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @BeforeEach
    void setUp() {
        // 2. Corregir la instanciación de entidades para que coincida con el modelo real
        Rol rolUsuario = new Rol(1L, "ROLE_USER");
        usuarioPrueba = new Usuario(1L, "usuarioTest", "passwordCodificado", Set.of(rolUsuario));
        fechaInicio = LocalDate.of(2023, 1, 1);
        fechaFin = LocalDate.of(2023, 1, 31);
    }

    @Test
    @DisplayName("Debería generar un reporte con ingresos y egresos correctos y agrupados por categoría")
    void generarReporteFinanciero_conTransacciones_deberiaDevolverReporteCorrecto() {
        // 3. Configurar el mock del servicio de seguridad (mucho más simple)
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));

        // Datos de prueba para transacciones
        Categoria catSalario = new Categoria(100L, "Salario", TipoTransaccion.INGRESO, usuarioPrueba);
        Categoria catComida = new Categoria(101L, "Comida", TipoTransaccion.EGRESO, usuarioPrueba);
        Categoria catTransporte = new Categoria(102L, "Transporte", TipoTransaccion.EGRESO, usuarioPrueba);
        Categoria catOcio = new Categoria(103L, "Ocio", TipoTransaccion.EGRESO, usuarioPrueba);

        // 4. Corregir la instanciación de Transaccion (sin el usuario al final)
        List<Transaccion> transacciones = Arrays.asList(
                new Transaccion(1L, new BigDecimal("2000.00"), TipoTransaccion.INGRESO, "Pago de salario", LocalDate.of(2023, 1, 15), catSalario),
                new Transaccion(2L, new BigDecimal("100.00"), TipoTransaccion.EGRESO, "Compra supermercado", LocalDate.of(2023, 1, 5), catComida),
                new Transaccion(3L, new BigDecimal("50.00"), TipoTransaccion.EGRESO, "Billete de bus", LocalDate.of(2023, 1, 10), catTransporte),
                new Transaccion(4L, new BigDecimal("75.00"), TipoTransaccion.EGRESO, "Cine", LocalDate.of(2023, 1, 20), catOcio),
                new Transaccion(5L, new BigDecimal("25.00"), TipoTransaccion.EGRESO, "Café", LocalDate.of(2023, 1, 22), catComida),
                new Transaccion(6L, new BigDecimal("500.00"), TipoTransaccion.INGRESO, "Venta de objeto", LocalDate.of(2023, 1, 25), catSalario)
        );

        // 5. Corregir el nombre del método del repositorio
        when(transaccionRepository.findAllByUsuarioAndFechaBetween(usuarioPrueba, fechaInicio, fechaFin)).thenReturn(transacciones);

        // Ejecuta el método
        ReporteFinancieroDTO reporte = reporteService.generarReporteFinanciero(fechaInicio, fechaFin);

        // Verifica el resultado
        assertNotNull(reporte);
        assertEquals(new BigDecimal("2500.00"), reporte.getTotalIngresos()); // 2000 + 500
        assertEquals(new BigDecimal("250.00"), reporte.getTotalEgresos());   // 100 + 50 + 75 + 25
        assertEquals(new BigDecimal("2250.00"), reporte.getBalanceNeto());  // 2500 - 250

        assertNotNull(reporte.getGastosPorCategoria());
        assertEquals(3, reporte.getGastosPorCategoria().size());
        assertEquals(new BigDecimal("125.00"), reporte.getGastosPorCategoria().get("Comida"));
        assertEquals(new BigDecimal("50.00"), reporte.getGastosPorCategoria().get("Transporte"));
        assertEquals(new BigDecimal("75.00"), reporte.getGastosPorCategoria().get("Ocio"));

        verify(transaccionRepository, times(1)).findAllByUsuarioAndFechaBetween(usuarioPrueba, fechaInicio, fechaFin);
    }

    @Test
    @DisplayName("Debería generar un reporte con cero en ingresos y egresos si no hay transacciones (caso de borde)")
    void generarReporteFinanciero_sinTransacciones_deberiaDevolverCero() {
        // No más MockedStatic
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));
        when(transaccionRepository.findAllByUsuarioAndFechaBetween(usuarioPrueba, fechaInicio, fechaFin)).thenReturn(Collections.emptyList());

        // Ejecuta el método
        ReporteFinancieroDTO reporte = reporteService.generarReporteFinanciero(fechaInicio, fechaFin);

        // Verifica el resultado
        assertNotNull(reporte);
        assertEquals(BigDecimal.ZERO, reporte.getTotalIngresos());
        assertEquals(BigDecimal.ZERO, reporte.getTotalEgresos());
        assertEquals(BigDecimal.ZERO, reporte.getBalanceNeto());
        assertTrue(reporte.getGastosPorCategoria().isEmpty());

        verify(transaccionRepository, times(1)).findAllByUsuarioAndFechaBetween(usuarioPrueba, fechaInicio, fechaFin);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario autenticado no se encuentra")
    void generarReporteFinanciero_usuarioNoEncontrado_deberiaLanzarExcepcion() {
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioInexistente");
        when(usuarioRepository.findByNombreUsuario("usuarioInexistente")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> reporteService.generarReporteFinanciero(fechaInicio, fechaFin));
        verify(transaccionRepository, never()).findAllByUsuarioAndFechaBetween(any(Usuario.class), any(LocalDate.class), any(LocalDate.class));
    }

    // ... Los otros tests (soloIngresos, soloEgresos) se refactorizarían de la misma manera,
    // eliminando MockedStatic y corrigiendo la creación de las entidades de prueba.
}
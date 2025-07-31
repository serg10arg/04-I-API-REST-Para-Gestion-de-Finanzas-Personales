package com.example.finanzaspersonales.servicio;

import com.example.finanzaspersonales.dto.agregacion.ReporteFinancieroDTO;
import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import com.example.finanzaspersonales.modelo.Transaccion;
import com.example.finanzaspersonales.modelo.Usuario;
import com.example.finanzaspersonales.repositorio.TransaccionRepository;
import com.example.finanzaspersonales.repositorio.UsuarioRepository;
import com.example.finanzaspersonales.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioSeguridad servicioSeguridad; // 1. Inyectar

    public ReporteService(TransaccionRepository transaccionRepository, UsuarioRepository usuarioRepository, ServicioSeguridad servicioSeguridad) { // 2. Añadir al constructor
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioSeguridad = servicioSeguridad;
    }

    private Usuario obtenerUsuarioAutenticado() {
        String nombreUsuario = servicioSeguridad.obtenerNombreUsuarioAutenticado();
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con nombre: " + nombreUsuario));
    }

    @Transactional(readOnly = true)
    public ReporteFinancieroDTO generarReporteFinanciero(LocalDate fechaInicio, LocalDate fechaFin) {
        Usuario usuario = obtenerUsuarioAutenticado(); // 3. Usar el método centralizado

        List<Transaccion> transacciones = transaccionRepository.findAllByUsuarioAndFechaBetween(usuario, fechaInicio, fechaFin);

        // ... resto de la lógica sin cambios ...
        BigDecimal totalIngresos = transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.INGRESO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEgresos = transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.EGRESO)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balanceNeto = totalIngresos.subtract(totalEgresos);

        Map<String, BigDecimal> gastosPorCategoria = transacciones.stream()
                .filter(t -> t.getTipo() == TipoTransaccion.EGRESO)
                .collect(Collectors.groupingBy(
                        t -> t.getCategoria().getNombre(),
                        Collectors.mapping(Transaccion::getMonto, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return new ReporteFinancieroDTO(totalIngresos, totalEgresos, balanceNeto, gastosPorCategoria);
    }
}
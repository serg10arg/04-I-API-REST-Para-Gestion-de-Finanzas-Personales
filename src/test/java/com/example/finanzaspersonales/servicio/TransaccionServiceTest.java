package com.example.finanzaspersonales.servicio;

import com.example.finanzaspersonales.dto.operaciones.TransaccionDTO;
import com.example.finanzaspersonales.exception.RecursoNoEncontradoException;
import com.example.finanzaspersonales.mapper.TransaccionMapper;
import com.example.finanzaspersonales.modelo.Categoria;
import com.example.finanzaspersonales.modelo.Transaccion;
import com.example.finanzaspersonales.modelo.enums.TipoTransaccion;
import com.example.finanzaspersonales.modelo.Rol;
import com.example.finanzaspersonales.modelo.Usuario;
import com.example.finanzaspersonales.repositorio.CategoriaRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para el TransaccionService.
 * Utiliza Mockito para simular dependencias como los repositorios.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para TransaccionService")
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private TransaccionMapper transaccionMapper;
    @Mock
    private ServicioSeguridad servicioSeguridad;

    @InjectMocks
    private TransaccionService transaccionService;

    private Usuario usuarioPrueba;
    private Categoria categoriaPrueba;
    private Transaccion transaccionPrueba;
    private TransaccionDTO transaccionDTOCreado;
    private TransaccionDTO transaccionDTOActualizado;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario(1L, "usuarioTest", "passwordCodificado", Collections.singleton(new Rol(1L, "ROLE_USER")));
        categoriaPrueba = new Categoria(101L, "Comida", TipoTransaccion.EGRESO, usuarioPrueba);
        transaccionPrueba = new Transaccion(1L, new BigDecimal("50.00"), TipoTransaccion.EGRESO, "Almuerzo", LocalDate.now(), categoriaPrueba);

        transaccionDTOCreado = new TransaccionDTO();
        transaccionDTOCreado.setMonto(new BigDecimal("50.00"));
        transaccionDTOCreado.setTipo(TipoTransaccion.EGRESO);
        transaccionDTOCreado.setDescripcion("Almuerzo");
        transaccionDTOCreado.setFecha(LocalDate.now());
        transaccionDTOCreado.setCategoriaId(101L);

        transaccionDTOActualizado = new TransaccionDTO();
        transaccionDTOActualizado.setMonto(new BigDecimal("60.00"));
        transaccionDTOActualizado.setTipo(TipoTransaccion.EGRESO);
        transaccionDTOActualizado.setDescripcion("Cena");
        transaccionDTOActualizado.setFecha(LocalDate.now());
        transaccionDTOActualizado.setCategoriaId(101L);
    }

    @Test
    @DisplayName("Debería crear una transacción exitosamente")
    void crearTransaccion_deberiaCrearTransaccion() {
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));
        when(categoriaRepository.findByIdAndUsuario(transaccionDTOCreado.getCategoriaId(), usuarioPrueba)).thenReturn(Optional.of(categoriaPrueba));
        when(transaccionMapper.aTransaccion(transaccionDTOCreado)).thenReturn(transaccionPrueba);
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionPrueba);
        when(transaccionMapper.aTransaccionDTO(transaccionPrueba)).thenReturn(transaccionDTOCreado);

        // Ejecuta el método
        TransaccionDTO resultado = transaccionService.crearTransaccion(transaccionDTOCreado);

        // Verifica el resultado
        assertNotNull(resultado);
        assertEquals(transaccionDTOCreado.getMonto(), resultado.getMonto());
        verify(transaccionRepository, times(1)).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción si la categoría no es del usuario al crear transacción")
    void crearTransaccion_categoriaNoEncontradaOnoDelUsuario_deberiaLanzarExcepcion() {
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));
        when(categoriaRepository.findByIdAndUsuario(transaccionDTOCreado.getCategoriaId(), usuarioPrueba)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> transaccionService.crearTransaccion(transaccionDTOCreado));
        verify(transaccionRepository, never()).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("Debería obtener una transacción por ID exitosamente")
    void obtenerTransaccionPorId_deberiaDevolverTransaccion() {
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));
        when(transaccionRepository.findByIdAndUsuario(1L, usuarioPrueba)).thenReturn(Optional.of(transaccionPrueba));
        when(transaccionMapper.aTransaccionDTO(transaccionPrueba)).thenReturn(transaccionDTOCreado);

        TransaccionDTO resultado = transaccionService.obtenerTransaccionPorId(1L);

        assertNotNull(resultado);
        assertEquals(transaccionDTOCreado.getDescripcion(), resultado.getDescripcion());
    }

    @Test
    @DisplayName("Debería lanzar excepción si la transacción no existe o no pertenece al usuario")
    void obtenerTransaccionPorId_noEncontradaOnoDelUsuario_deberiaLanzarExcepcion() {
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));
        when(transaccionRepository.findByIdAndUsuario(1L, usuarioPrueba)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> transaccionService.obtenerTransaccionPorId(1L));
    }

    @Test
    @DisplayName("Debería obtener todas las transacciones del usuario exitosamente")
    void obtenerTransaccionesDelUsuario_deberiaDevolverLista() {
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));

        List<Transaccion> transacciones = Arrays.asList(transaccionPrueba);
        List<TransaccionDTO> transaccionesDTO = Arrays.asList(transaccionDTOCreado);

        // Corregir la llamada al método del repositorio
        when(transaccionRepository.findByCategoriaUsuario(usuarioPrueba)).thenReturn(transacciones);
        when(transaccionMapper.aTransaccionDTO(transaccionPrueba)).thenReturn(transaccionDTOCreado);

        List<TransaccionDTO> resultado = transaccionService.obtenerTransaccionesDelUsuario();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(transaccionesDTO.get(0).getDescripcion(), resultado.get(0).getDescripcion());
    }

    @Test
    @DisplayName("Debería actualizar una transacción existente exitosamente")
    void actualizarTransaccion_deberiaActualizarTransaccion() {
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));

        // Una transacción existente con la categoría original
        Transaccion transaccionExistente = new Transaccion(1L, new BigDecimal("50.00"), TipoTransaccion.EGRESO, "Almuerzo original", LocalDate.now(), categoriaPrueba);
        Categoria nuevaCategoria = new Categoria(102L, "Transporte", TipoTransaccion.EGRESO, usuarioPrueba);
        TransaccionDTO transaccionDTOActualizadoConNuevaCategoria = new TransaccionDTO();
        transaccionDTOActualizadoConNuevaCategoria.setMonto(new BigDecimal("70.00"));
        transaccionDTOActualizadoConNuevaCategoria.setTipo(TipoTransaccion.EGRESO);
        transaccionDTOActualizadoConNuevaCategoria.setDescripcion("Viaje en bus");
        transaccionDTOActualizadoConNuevaCategoria.setFecha(LocalDate.now());
        transaccionDTOActualizadoConNuevaCategoria.setCategoriaId(102L); // Nueva categoría ID

        when(transaccionRepository.findByIdAndUsuario(1L, usuarioPrueba)).thenReturn(Optional.of(transaccionExistente));
        when(categoriaRepository.findByIdAndUsuario(102L, usuarioPrueba)).thenReturn(Optional.of(nuevaCategoria));
        doAnswer(invocation -> {
            TransaccionDTO dto = invocation.getArgument(0);
            Transaccion target = invocation.getArgument(1);
            target.setMonto(dto.getMonto());
            target.setTipo(dto.getTipo());
            target.setDescripcion(dto.getDescripcion());
            target.setFecha(dto.getFecha());
            return null;
        }).when(transaccionMapper).actualizarTransaccionDesdeDTO(any(TransaccionDTO.class), any(Transaccion.class));
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionExistente); // Devuelve la misma instancia después de la actualización
        when(transaccionMapper.aTransaccionDTO(transaccionExistente)).thenReturn(transaccionDTOActualizadoConNuevaCategoria);


        TransaccionDTO resultado = transaccionService.actualizarTransaccion(1L, transaccionDTOActualizadoConNuevaCategoria);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("70.00"), resultado.getMonto());
        assertEquals("Viaje en bus", resultado.getDescripcion());
        assertEquals(102L, resultado.getCategoriaId());
        verify(transaccionRepository, times(1)).save(transaccionExistente);
    }

    @Test
    @DisplayName("Debería eliminar una transacción exitosamente")
    void eliminarTransaccion_deberiaEliminarTransaccion() {
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));
        when(transaccionRepository.findByIdAndUsuario(1L, usuarioPrueba)).thenReturn(Optional.of(transaccionPrueba));
        doNothing().when(transaccionRepository).delete(transaccionPrueba);

        transaccionService.eliminarTransaccion(1L);

        verify(transaccionRepository, times(1)).delete(transaccionPrueba);
    }

    @Test
    @DisplayName("Debería lanzar excepción si la transacción no existe o no pertenece al usuario al eliminar")
    void eliminarTransaccion_noEncontradaOnoDelUsuario_deberiaLanzarExcepcion() {
        when(servicioSeguridad.obtenerNombreUsuarioAutenticado()).thenReturn("usuarioTest");
        when(usuarioRepository.findByNombreUsuario("usuarioTest")).thenReturn(Optional.of(usuarioPrueba));
        when(transaccionRepository.findByIdAndUsuario(1L, usuarioPrueba)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> transaccionService.eliminarTransaccion(1L));
        verify(transaccionRepository, never()).delete(any(Transaccion.class));
    }
}


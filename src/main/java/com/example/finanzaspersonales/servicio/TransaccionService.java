package com.example.finanzaspersonales.servicio;

import com.example.finanzaspersonales.dto.operaciones.TransaccionDTO;
import com.example.finanzaspersonales.exception.RecursoNoEncontradoException;
import com.example.finanzaspersonales.mapper.TransaccionMapper;
import com.example.finanzaspersonales.modelo.Categoria;
import com.example.finanzaspersonales.modelo.Transaccion;
import com.example.finanzaspersonales.modelo.Usuario;
import com.example.finanzaspersonales.repositorio.CategoriaRepository;
import com.example.finanzaspersonales.repositorio.TransaccionRepository;
import com.example.finanzaspersonales.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de transacciones (ingresos y egresos).
 * Asegura que los usuarios solo puedan gestionar sus propias transacciones.
 */
@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final TransaccionMapper transaccionMapper;
    private final ServicioSeguridad servicioSeguridad; // 1. Inyectar el servicio de seguridad

    public TransaccionService(TransaccionRepository transaccionRepository,
                              UsuarioRepository usuarioRepository,
                              CategoriaRepository categoriaRepository,
                              TransaccionMapper transaccionMapper,
                              ServicioSeguridad servicioSeguridad) { // 2. Añadir al constructor
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.transaccionMapper = transaccionMapper;
        this.servicioSeguridad = servicioSeguridad;
    }

    // 3. Centralizar la obtención del usuario en un método privado
    private Usuario obtenerUsuarioAutenticado() {
        String nombreUsuario = servicioSeguridad.obtenerNombreUsuarioAutenticado();
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con nombre: " + nombreUsuario));
    }

    /**
     * Crea una nueva transacción (ingreso o egreso) para el usuario autenticado.
     * @param transaccionDTO El DTO de la transacción a crear.
     * @return El TransaccionDTO de la transacción creada.
     * @throws RecursoNoEncontradoException si el usuario o la categoría no se encuentran o la categoría no pertenece al usuario.
     */
    @Transactional
    public TransaccionDTO crearTransaccion(TransaccionDTO transaccionDTO) {
        Usuario usuario = obtenerUsuarioAutenticado(); // 4. Usar el método centralizado

        Categoria categoria = categoriaRepository.findByIdAndUsuario(transaccionDTO.getCategoriaId(), usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada o no pertenece a este usuario."));

        Transaccion transaccion = transaccionMapper.aTransaccion(transaccionDTO);
        // La entidad Transaccion no tiene una relación directa con Usuario, se infiere a través de Categoria.
        // Si se quisiera una relación directa, se debería añadir a la entidad Transaccion.
        // Por ahora, la seguridad se garantiza porque la categoría pertenece al usuario.
        transaccion.setCategoria(categoria);
        transaccion = transaccionRepository.save(transaccion);
        return transaccionMapper.aTransaccionDTO(transaccion);
    }

    /**
     * Obtiene una transacción por su ID, asegurando que pertenezca al usuario autenticado.
     * @param id El ID de la transacción.
     * @return El TransaccionDTO de la transacción.
     * @throws RecursoNoEncontradoException si la transacción no existe o no pertenece al usuario autenticado.
     */
    @Transactional(readOnly = true)
    public TransaccionDTO obtenerTransaccionPorId(Long id) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Transaccion transaccion = transaccionRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada o no pertenece a este usuario."));
        return transaccionMapper.aTransaccionDTO(transaccion);
    }

    /**
     * Obtiene todas las transacciones del usuario autenticado.
     * @return Una lista de TransaccionDTOs.
     */
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesDelUsuario() {
        Usuario usuario = obtenerUsuarioAutenticado();

        List<Transaccion> transacciones = transaccionRepository.findByCategoriaUsuario(usuario);
        return transacciones.stream()
                .map(transaccionMapper::aTransaccionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una transacción existente, asegurando que pertenezca al usuario autenticado.
     * @param id El ID de la transacción a actualizar.
     * @param transaccionDTO El DTO con los datos actualizados de la transacción.
     * @return El TransaccionDTO de la transacción actualizada.
     * @throws RecursoNoEncontradoException si la transacción o la categoría no existe o no pertenece al usuario autenticado.
     */
    @Transactional
    public TransaccionDTO actualizarTransaccion(Long id, TransaccionDTO transaccionDTO) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Transaccion transaccionExistente = transaccionRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada o no pertenece a este usuario."));

        Categoria nuevaCategoria = categoriaRepository.findByIdAndUsuario(transaccionDTO.getCategoriaId(), usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada o no pertenece a este usuario."));

        transaccionMapper.actualizarTransaccionDesdeDTO(transaccionDTO, transaccionExistente);
        transaccionExistente.setCategoria(nuevaCategoria); // Actualiza la categoría
        transaccionExistente = transaccionRepository.save(transaccionExistente);
        return transaccionMapper.aTransaccionDTO(transaccionExistente);
    }

    /**
     * Elimina una transacción por su ID, asegurando que pertenezca al usuario autenticado.
     * @param id El ID de la transacción a eliminar.
     * @throws RecursoNoEncontradoException si la transacción no existe o no pertenece al usuario autenticado.
     */
    @Transactional
    public void eliminarTransaccion(Long id) {
        Usuario usuario = obtenerUsuarioAutenticado();

        Transaccion transaccion = transaccionRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada o no pertenece a este usuario."));

        transaccionRepository.delete(transaccion);
    }
}
package com.example.finanzaspersonales.servicio;

import com.example.finanzaspersonales.dto.operaciones.CategoriaDTO;
import com.example.finanzaspersonales.exception.RecursoNoEncontradoException;
import com.example.finanzaspersonales.mapper.CategoriaMapper;
import com.example.finanzaspersonales.modelo.Categoria;
import com.example.finanzaspersonales.modelo.Usuario;
import com.example.finanzaspersonales.repositorio.CategoriaRepository;
import com.example.finanzaspersonales.repositorio.UsuarioRepository;
// import com.example.finanzaspersonales.servicio.ServicioSeguridad; // Ya está importado
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaMapper categoriaMapper;
    private final ServicioSeguridad servicioSeguridad; // 1. Inyectar el servicio

    public CategoriaService(CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository,
                            CategoriaMapper categoriaMapper, ServicioSeguridad servicioSeguridad) { // 2. Añadir al constructor
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaMapper = categoriaMapper;
        this.servicioSeguridad = servicioSeguridad;
    }

    // Método privado para centralizar la obtención del usuario
    private Usuario obtenerUsuarioAutenticado() {
        String nombreUsuario = servicioSeguridad.obtenerNombreUsuarioAutenticado();
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con nombre: " + nombreUsuario));
    }

    @Transactional
    public CategoriaDTO crearCategoria(CategoriaDTO categoriaDTO) {
        Usuario usuario = obtenerUsuarioAutenticado(); // 3. Usar el método centralizado

        if (categoriaRepository.existsByNombreAndTipoAndUsuario(categoriaDTO.getNombre(), categoriaDTO.getTipo(), usuario)) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre y tipo para este usuario.");
        }

        Categoria categoria = categoriaMapper.aCategoria(categoriaDTO);
        categoria.setUsuario(usuario);
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.aCategoriaDTO(categoria);
    }

    @Transactional(readOnly = true)
    public CategoriaDTO obtenerCategoriaPorId(Long id) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Categoria categoria = categoriaRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada o no pertenece a este usuario."));
        return categoriaMapper.aCategoriaDTO(categoria);
    }

    // ... (Aplicar el mismo cambio a los demás métodos: obtenerCategoriasDelUsuario, actualizarCategoria, eliminarCategoria)
    // ...

    @Transactional(readOnly = true)
    public List<CategoriaDTO> obtenerCategoriasDelUsuario() {
        Usuario usuario = obtenerUsuarioAutenticado();
        List<Categoria> categorias = categoriaRepository.findByUsuario(usuario);
        return categorias.stream()
                .map(categoriaMapper::aCategoriaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Categoria categoria = categoriaRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada o no pertenece a este usuario."));
        categoriaRepository.delete(categoria);
    }

    // El método actualizarCategoria también debe ser refactorizado de la misma manera
    @Transactional
    public CategoriaDTO actualizarCategoria(Long id, CategoriaDTO categoriaDTO) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Categoria categoriaExistente = categoriaRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada o no pertenece a este usuario."));

        // Lógica de validación...
        if (categoriaRepository.existsByNombreAndTipoAndUsuario(categoriaDTO.getNombre(), categoriaDTO.getTipo(), usuario) &&
                (!categoriaExistente.getNombre().equals(categoriaDTO.getNombre()) ||
                        !categoriaExistente.getTipo().equals(categoriaDTO.getTipo()))) {
            throw new IllegalArgumentException("Ya existe otra categoría con el mismo nombre y tipo para este usuario.");
        }

        categoriaMapper.actualizarCategoriaDesdeDTO(categoriaDTO, categoriaExistente);
        categoriaExistente = categoriaRepository.save(categoriaExistente);
        return categoriaMapper.aCategoriaDTO(categoriaExistente);
    }
}
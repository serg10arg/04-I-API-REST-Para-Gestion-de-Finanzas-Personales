package com.example.finanzaspersonales.controladores;

import com.example.finanzaspersonales.dto.operaciones.CategoriaDTO;
import com.example.finanzaspersonales.servicio.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de categorías.
 * Sigue las convenciones RESTful. Los endpoints están protegidos y
 * aseguran que un usuario solo pueda acceder y modificar sus propias categorías.
 */
@RestController
@RequestMapping("/api/categorias")
@SecurityRequirement(name = "bearerAuth") // Indica que este controlador requiere autenticación JWT
@Tag(name = "Gestión de Categorías", description = "Endpoints CRUD para categorías de gastos e ingresos.")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /**
     * Crea una nueva categoría para el usuario autenticado.
     * URL: POST /api/categorias
     * @param categoriaDTO DTO de la categoría a crear.
     * @return ResponseEntity con la categoría creada.
     */
    @Operation(summary = "Crea una nueva categoría",
            description = "Permite al usuario autenticado definir una nueva categoría de gasto o ingreso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de categoría inválidos o ya existe para el usuario"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<CategoriaDTO> crearCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO nuevaCategoria = categoriaService.crearCategoria(categoriaDTO);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    /**
     * Obtiene una categoría específica por ID para el usuario autenticado.
     * URL: GET /api/categorias/{id}
     * @param id ID de la categoría.
     * @return ResponseEntity con la categoría encontrada.
     */
    @Operation(summary = "Obtiene una categoría por ID",
            description = "Recupera los detalles de una categoría específica del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada o no pertenece al usuario"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        CategoriaDTO categoria = categoriaService.obtenerCategoriaPorId(id);
        return ResponseEntity.ok(categoria);
    }

    /**
     * Obtiene todas las categorías del usuario autenticado.
     * URL: GET /api/categorias
     * @return ResponseEntity con una lista de categorías.
     */
    @Operation(summary = "Obtiene todas las categorías del usuario",
            description = "Recupera una lista de todas las categorías definidas por el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías recuperada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerTodasLasCategorias() {
        List<CategoriaDTO> categorias = categoriaService.obtenerCategoriasDelUsuario();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Actualiza una categoría existente para el usuario autenticado.
     * URL: PUT /api/categorias/{id}
     * @param id ID de la categoría a actualizar.
     * @param categoriaDTO DTO con los datos actualizados.
     * @return ResponseEntity con la categoría actualizada.
     */
    @Operation(summary = "Actualiza una categoría existente",
            description = "Modifica los detalles de una categoría existente del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de categoría inválidos o conflicto"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada o no pertenece al usuario"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO categoriaActualizada = categoriaService.actualizarCategoria(id, categoriaDTO);
        return ResponseEntity.ok(categoriaActualizada);
    }

    /**
     * Elimina una categoría específica para el usuario autenticado.
     * URL: DELETE /api/categorias/{id}
     * @param id ID de la categoría a eliminar.
     * @return ResponseEntity sin contenido.
     */
    @Operation(summary = "Elimina una categoría",
            description = "Elimina una categoría específica del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada o no pertenece al usuario"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}


package com.example.finanzaspersonales.controladores;

import com.example.finanzaspersonales.dto.operaciones.TransaccionDTO;
import com.example.finanzaspersonales.servicio.TransaccionService;
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
 * Controlador REST para la gestión de transacciones (ingresos y egresos).
 * Sigue las convenciones RESTful. Los endpoints están protegidos y
 * aseguran que un usuario solo pueda acceder y modificar sus propias transacciones.
 */
@RestController
@RequestMapping("/api/transacciones")
@SecurityRequirement(name = "bearerAuth") // Indica que este controlador requiere autenticación JWT
@Tag(name = "Gestión de Transacciones", description = "Endpoints CRUD para transacciones (ingresos y egresos).")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    /**
     * Crea una nueva transacción para el usuario autenticado.
     * URL: POST /api/transacciones
     * @param transaccionDTO DTO de la transacción a crear.
     * @return ResponseEntity con la transacción creada.
     */
    @Operation(summary = "Crea una nueva transacción",
            description = "Permite al usuario autenticado registrar un nuevo ingreso o egreso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transacción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de transacción inválidos"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada o no pertenece al usuario"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<TransaccionDTO> crearTransaccion(@Valid @RequestBody TransaccionDTO transaccionDTO) {
        TransaccionDTO nuevaTransaccion = transaccionService.crearTransaccion(transaccionDTO);
        return new ResponseEntity<>(nuevaTransaccion, HttpStatus.CREATED);
    }

    /**
     * Obtiene una transacción específica por ID para el usuario autenticado.
     * URL: GET /api/transacciones/{id}
     * @param id ID de la transacción.
     * @return ResponseEntity con la transacción encontrada.
     */
    @Operation(summary = "Obtiene una transacción por ID",
            description = "Recupera los detalles de una transacción específica del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción encontrada"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada o no pertenece al usuario"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransaccionDTO> obtenerTransaccionPorId(@PathVariable Long id) {
        TransaccionDTO transaccion = transaccionService.obtenerTransaccionPorId(id);
        return ResponseEntity.ok(transaccion);
    }

    /**
     * Obtiene todas las transacciones del usuario autenticado.
     * URL: GET /api/transacciones
     * @return ResponseEntity con una lista de transacciones.
     */
    @Operation(summary = "Obtiene todas las transacciones del usuario",
            description = "Recupera una lista de todas las transacciones (ingresos y egresos) del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transacciones recuperada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public ResponseEntity<List<TransaccionDTO>> obtenerTodasLasTransacciones() {
        List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesDelUsuario();
        return ResponseEntity.ok(transacciones);
    }

    /**
     * Actualiza una transacción existente para el usuario autenticado.
     * URL: PUT /api/transacciones/{id}
     * @param id ID de la transacción a actualizar.
     * @param transaccionDTO DTO con los datos actualizados.
     * @return ResponseEntity con la transacción actualizada.
     */
    @Operation(summary = "Actualiza una transacción existente",
            description = "Modifica los detalles de una transacción existente del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de transacción inválidos"),
            @ApiResponse(responseCode = "404", description = "Transacción o categoría no encontrada o no pertenece al usuario"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransaccionDTO> actualizarTransaccion(@PathVariable Long id, @Valid @RequestBody TransaccionDTO transaccionDTO) {
        TransaccionDTO transaccionActualizada = transaccionService.actualizarTransaccion(id, transaccionDTO);
        return ResponseEntity.ok(transaccionActualizada);
    }

    /**
     * Elimina una transacción específica para el usuario autenticado.
     * URL: DELETE /api/transacciones/{id}
     * @param id ID de la transacción a eliminar.
     * @return ResponseEntity sin contenido.
     */
    @Operation(summary = "Elimina una transacción",
            description = "Elimina una transacción específica del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transacción eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada o no pertenece al usuario"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransaccion(@PathVariable Long id) {
        transaccionService.eliminarTransaccion(id);
        return ResponseEntity.noContent().build();
    }
}


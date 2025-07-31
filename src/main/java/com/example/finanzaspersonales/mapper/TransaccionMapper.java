package com.example.finanzaspersonales.mapper;

import com.example.finanzaspersonales.dto.operaciones.TransaccionDTO;
import com.example.finanzaspersonales.modelo.Transaccion;
import com.example.finanzaspersonales.dto.operaciones.TransaccionRespuestaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interfaz de mapeo para convertir entre la entidad Transaccion y TransaccionDTO.
 * Utiliza MapStruct para generar automáticamente el código de mapeo.
 * Se especifica componentModel = "spring" para que MapStruct genere un bean Spring.
 */
@Mapper(componentModel = "spring", uses = {CategoriaMapper.class})
public interface TransaccionMapper {

    /**
     * Convierte una entidad Transaccion a su DTO correspondiente.
     * Mapea la categoría al ID de categoría.
     * @param transaccion La entidad Transaccion.
     * @return El TransaccionDTO resultante.
     */
    @Mapping(source = "categoria.id", target = "categoriaId")
    TransaccionDTO aTransaccionDTO(Transaccion transaccion);

    /**
     * Convierte un TransaccionDTO a la entidad Transaccion.
     * Es importante manejar la asignación de la categoría y el usuario en el servicio, no aquí.
     * @param transaccionDTO El TransaccionDTO.
     * @return La entidad Transaccion resultante.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true) // La categoría se asignará en el servicio
    Transaccion aTransaccion(TransaccionDTO transaccionDTO);

    /**
     * Actualiza una entidad Transaccion existente con los datos de un TransaccionDTO.
     * @param transaccionDTO El TransaccionDTO con los datos actualizados.
     * @param transaccion La entidad Transaccion a actualizar.
     */
    @Mapping(target = "id", ignore = true) // Ignorar el ID para actualizaciones
    @Mapping(target = "categoria", ignore = true) // La categoría se reasignará en el servicio
    void actualizarTransaccionDesdeDTO(TransaccionDTO transaccionDTO, @MappingTarget Transaccion transaccion);

    /**
     * Convierte una entidad Transaccion a un DTO de respuesta.
     * Este DTO incluye el objeto CategoriaDTO, evitando que el cliente
     * necesite hacer llamadas adicionales a la API
     * @param transaccion La entidad Transaccion
     * @return El TransaccionRespuestaDTO resultante
     */
    TransaccionRespuestaDTO aTransaccionRespuestaDTO(Transaccion transaccion);

}


package com.example.finanzaspersonales.mapper;

import com.example.finanzaspersonales.dto.operaciones.CategoriaDTO;
import com.example.finanzaspersonales.modelo.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interfaz de mapeo para convertir entre la entidad Categoria y CategoriaDTO.
 * Utiliza MapStruct para generar automáticamente el código de mapeo.
 * Se especifica componentModel = "spring" para que MapStruct genere un bean Spring.
 */
@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    /**
     * Convierte una entidad Categoria a su DTO correspondiente.
     * @param categoria La entidad Categoria.
     * @return El CategoriaDTO resultante.
     */
    CategoriaDTO aCategoriaDTO(Categoria categoria);

    /**
     * Convierte un CategoriaDTO a la entidad Categoria.
     * Es importante manejar la asignación del usuario en el servicio, no aquí directamente.
     * @param categoriaDTO El CategoriaDTO.
     * @return La entidad Categoria resultante.
     */
    Categoria aCategoria(CategoriaDTO categoriaDTO);

    /**
     * Actualiza una entidad Categoria existente con los datos de un CategoriaDTO.
     * @param categoriaDTO El CategoriaDTO con los datos actualizados.
     * @param categoria La entidad Categoria a actualizar.
     */
    @Mapping(target = "id", ignore = true) // Ignorar el ID para actualizaciones
    @Mapping(target = "usuario", ignore = true) // Ignorar el usuario para actualizaciones, se gestiona en el servicio
    void actualizarCategoriaDesdeDTO(CategoriaDTO categoriaDTO, @MappingTarget Categoria categoria);
}


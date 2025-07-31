package com.example.finanzaspersonales.mapper;

import com.example.finanzaspersonales.dto.autenticacion.UsuarioRegistroDTO;
import com.example.finanzaspersonales.modelo.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interfaz de mapeo para convertir entre la entidad Usuario y UsuarioRegistroDTO.
 * Utiliza MapStruct para generar automáticamente el código de mapeo.
 * Se especifica componentModel = "spring" para que MapStruct genere un bean Spring.
 * Es crucial ignorar la contraseña en el mapeo a DTOs de salida para seguridad.
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    /**
     * Convierte un UsuarioRegistroDTO a una entidad Usuario.
     * La contraseña se debe codificar en el servicio, no en el mapeador.
     * Los roles se pueden establecer por defecto o manejar en el servicio.
     * @param usuarioRegistroDTO El DTO de registro.
     * @return La entidad Usuario resultante.
     */
    @Mapping(target = "id", ignore = true) // ID autogenerado
    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "roles", ignore = true) // Roles se asignarán más tarde
    @Mapping(target = "categorias", ignore = true) // Las categorías se asignarán más tarde
    Usuario aUsuario(UsuarioRegistroDTO usuarioRegistroDTO);
}


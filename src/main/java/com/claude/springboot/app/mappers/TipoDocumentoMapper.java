package com.claude.springboot.app.mappers;

import com.claude.springboot.app.dto.TipoDocumentoDTO;
import com.claude.springboot.app.entities.TipoDocumento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades TipoDocumento y DTOs
 * 
 * @author Sistema PQRS
 * @version 1.0
 */
@Component
public class TipoDocumentoMapper {

    /**
     * Convierte una entidad TipoDocumento a DTO
     * 
     * @param tipoDocumento Entidad a convertir
     * @return DTO convertido o null si la entidad es null
     */
    public TipoDocumentoDTO toDTO(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null) {
            return null;
        }
        
        TipoDocumentoDTO dto = new TipoDocumentoDTO();
        dto.setId(tipoDocumento.getId());
        dto.setCodigo(tipoDocumento.getCodigo());
        dto.setNombre(tipoDocumento.getNombre());
        dto.setDescripcion(tipoDocumento.getDescripcion());
        dto.setEstado(tipoDocumento.getEstado());
        dto.setFechaCreacion(tipoDocumento.getFechaCreacion());
        dto.setFechaActualizacion(tipoDocumento.getFechaActualizacion());
        
        return dto;
    }

    /**
     * Convierte un DTO a entidad TipoDocumento
     * 
     * @param tipoDocumentoDTO DTO a convertir
     * @return Entidad convertida o null si el DTO es null
     */
    public TipoDocumento toEntity(TipoDocumentoDTO tipoDocumentoDTO) {
        if (tipoDocumentoDTO == null) {
            return null;
        }
        
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setId(tipoDocumentoDTO.getId());
        tipoDocumento.setCodigo(tipoDocumentoDTO.getCodigo());
        tipoDocumento.setNombre(tipoDocumentoDTO.getNombre());
        tipoDocumento.setDescripcion(tipoDocumentoDTO.getDescripcion());
        tipoDocumento.setEstado(tipoDocumentoDTO.getEstado());
        tipoDocumento.setFechaCreacion(tipoDocumentoDTO.getFechaCreacion());
        tipoDocumento.setFechaActualizacion(tipoDocumentoDTO.getFechaActualizacion());
        
        return tipoDocumento;
    }

    /**
     * Convierte una lista de entidades TipoDocumento a lista de DTOs
     * 
     * @param tiposDocumento Lista de entidades a convertir
     * @return Lista de DTOs convertidos
     */
    public List<TipoDocumentoDTO> toDTOList(List<TipoDocumento> tiposDocumento) {
        if (tiposDocumento == null) {
            return null;
        }
        
        return tiposDocumento.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de DTOs a lista de entidades TipoDocumento
     * 
     * @param tiposDocumentoDTO Lista de DTOs a convertir
     * @return Lista de entidades convertidas
     */
    public List<TipoDocumento> toEntityList(List<TipoDocumentoDTO> tiposDocumentoDTO) {
        if (tiposDocumentoDTO == null) {
            return null;
        }
        
        return tiposDocumentoDTO.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una entidad existente con los datos del DTO
     * 
     * @param tipoDocumentoExistente Entidad existente a actualizar
     * @param tipoDocumentoDTO       DTO con los nuevos datos
     * @return Entidad actualizada
     */
    public TipoDocumento updateEntity(TipoDocumento tipoDocumentoExistente, TipoDocumentoDTO tipoDocumentoDTO) {
        if (tipoDocumentoExistente == null || tipoDocumentoDTO == null) {
            return tipoDocumentoExistente;
        }
        
        tipoDocumentoExistente.setCodigo(tipoDocumentoDTO.getCodigo());
        tipoDocumentoExistente.setNombre(tipoDocumentoDTO.getNombre());
        tipoDocumentoExistente.setDescripcion(tipoDocumentoDTO.getDescripcion());
        tipoDocumentoExistente.setEstado(tipoDocumentoDTO.getEstado());
        
        // No actualizamos fechas aquí, se manejan automáticamente con @PreUpdate
        
        return tipoDocumentoExistente;
    }

    /**
     * Crea una nueva entidad para inserción (sin ID ni fechas)
     * 
     * @param tipoDocumentoDTO DTO con los datos para la nueva entidad
     * @return Nueva entidad para inserción
     */
    public TipoDocumento toNewEntity(TipoDocumentoDTO tipoDocumentoDTO) {
        if (tipoDocumentoDTO == null) {
            return null;
        }
        
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setCodigo(tipoDocumentoDTO.getCodigo());
        tipoDocumento.setNombre(tipoDocumentoDTO.getNombre());
        tipoDocumento.setDescripcion(tipoDocumentoDTO.getDescripcion());
        tipoDocumento.setEstado(tipoDocumentoDTO.getEstado() != null ? tipoDocumentoDTO.getEstado() : true);
        
        // ID y fechas se asignan automáticamente
        
        return tipoDocumento;
    }

    /**
     * Crea un DTO básico con solo código y nombre
     * 
     * @param codigo Código del tipo de documento
     * @param nombre Nombre del tipo de documento
     * @return DTO básico
     */
    public TipoDocumentoDTO createBasicDTO(String codigo, String nombre) {
        return new TipoDocumentoDTO(codigo, nombre);
    }

    /**
     * Crea un DTO completo
     * 
     * @param codigo      Código del tipo de documento
     * @param nombre      Nombre del tipo de documento
     * @param descripcion Descripción del tipo de documento
     * @param estado      Estado del tipo de documento
     * @return DTO completo
     */
    public TipoDocumentoDTO createCompleteDTO(String codigo, String nombre, String descripcion, Boolean estado) {
        return new TipoDocumentoDTO(codigo, nombre, descripcion, estado);
    }
}

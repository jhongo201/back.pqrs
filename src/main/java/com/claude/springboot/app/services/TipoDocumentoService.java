package com.claude.springboot.app.services;

import com.claude.springboot.app.dto.TipoDocumentoDTO;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para TipoDocumento
 * 
 * @author Sistema PQRS
 * @version 1.0
 */
public interface TipoDocumentoService {

    /**
     * Obtener todos los tipos de documento
     * 
     * @return Lista de tipos de documento
     */
    List<TipoDocumentoDTO> obtenerTodosLosTiposDocumento();

    /**
     * Obtener todos los tipos de documento activos
     * 
     * @return Lista de tipos de documento activos
     */
    List<TipoDocumentoDTO> obtenerTiposDocumentoActivos();

    /**
     * Obtener tipo de documento por ID
     * 
     * @param id ID del tipo de documento
     * @return Optional con el tipo de documento si existe
     */
    Optional<TipoDocumentoDTO> obtenerTipoDocumentoPorId(Long id);

    /**
     * Obtener tipo de documento por código
     * 
     * @param codigo Código del tipo de documento
     * @return Optional con el tipo de documento si existe
     */
    Optional<TipoDocumentoDTO> obtenerTipoDocumentoPorCodigo(String codigo);

    /**
     * Obtener tipo de documento por nombre
     * 
     * @param nombre Nombre del tipo de documento
     * @return Optional con el tipo de documento si existe
     */
    Optional<TipoDocumentoDTO> obtenerTipoDocumentoPorNombre(String nombre);

    /**
     * Buscar tipos de documento por nombre
     * 
     * @param nombre Texto a buscar en el nombre
     * @return Lista de tipos de documento que coinciden
     */
    List<TipoDocumentoDTO> buscarTiposDocumentoPorNombre(String nombre);

    /**
     * Buscar tipos de documento por código o nombre
     * 
     * @param termino Término de búsqueda
     * @return Lista de tipos de documento que coinciden
     */
    List<TipoDocumentoDTO> buscarTiposDocumento(String termino);

    /**
     * Buscar tipos de documento activos por término
     * 
     * @param termino Término de búsqueda
     * @return Lista de tipos de documento activos que coinciden
     */
    List<TipoDocumentoDTO> buscarTiposDocumentoActivos(String termino);

    /**
     * Crear un nuevo tipo de documento
     * 
     * @param tipoDocumentoDTO Datos del tipo de documento a crear
     * @return Tipo de documento creado
     */
    TipoDocumentoDTO crearTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO);

    /**
     * Actualizar un tipo de documento existente
     * 
     * @param id               ID del tipo de documento a actualizar
     * @param tipoDocumentoDTO Nuevos datos del tipo de documento
     * @return Tipo de documento actualizado
     */
    Optional<TipoDocumentoDTO> actualizarTipoDocumento(Long id, TipoDocumentoDTO tipoDocumentoDTO);

    /**
     * Activar un tipo de documento
     * 
     * @param id ID del tipo de documento a activar
     * @return true si se activó correctamente, false en caso contrario
     */
    boolean activarTipoDocumento(Long id);

    /**
     * Desactivar un tipo de documento
     * 
     * @param id ID del tipo de documento a desactivar
     * @return true si se desactivó correctamente, false en caso contrario
     */
    boolean desactivarTipoDocumento(Long id);

    /**
     * Eliminar un tipo de documento
     * 
     * @param id ID del tipo de documento a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean eliminarTipoDocumento(Long id);

    /**
     * Verificar si existe un tipo de documento con el código especificado
     * 
     * @param codigo Código a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeTipoDocumentoPorCodigo(String codigo);

    /**
     * Verificar si existe un tipo de documento con el nombre especificado
     * 
     * @param nombre Nombre a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeTipoDocumentoPorNombre(String nombre);

    /**
     * Obtener estadísticas de tipos de documento
     * 
     * @return Array con [total, activos, inactivos]
     */
    Long[] obtenerEstadisticas();

    /**
     * Obtener el conteo total de tipos de documento
     * 
     * @return Número total de tipos de documento
     */
    long contarTiposDocumento();

    /**
     * Obtener el conteo de tipos de documento activos
     * 
     * @return Número de tipos de documento activos
     */
    long contarTiposDocumentoActivos();

    /**
     * Obtener el conteo de tipos de documento inactivos
     * 
     * @return Número de tipos de documento inactivos
     */
    long contarTiposDocumentoInactivos();
}

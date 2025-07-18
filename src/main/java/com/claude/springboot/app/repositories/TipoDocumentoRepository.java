package com.claude.springboot.app.repositories;

import com.claude.springboot.app.entities.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad TipoDocumento
 * 
 * @author Sistema PQRS
 * @version 1.0
 */
@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {

    /**
     * Buscar tipo de documento por código
     * 
     * @param codigo Código del tipo de documento
     * @return Optional con el tipo de documento si existe
     */
    Optional<TipoDocumento> findByCodigo(String codigo);

    /**
     * Buscar tipo de documento por código ignorando mayúsculas/minúsculas
     * 
     * @param codigo Código del tipo de documento
     * @return Optional con el tipo de documento si existe
     */
    Optional<TipoDocumento> findByCodigoIgnoreCase(String codigo);

    /**
     * Buscar tipo de documento por nombre
     * 
     * @param nombre Nombre del tipo de documento
     * @return Optional con el tipo de documento si existe
     */
    Optional<TipoDocumento> findByNombre(String nombre);

    /**
     * Buscar tipo de documento por nombre ignorando mayúsculas/minúsculas
     * 
     * @param nombre Nombre del tipo de documento
     * @return Optional con el tipo de documento si existe
     */
    Optional<TipoDocumento> findByNombreIgnoreCase(String nombre);

    /**
     * Obtener todos los tipos de documento activos
     * 
     * @return Lista de tipos de documento activos
     */
    List<TipoDocumento> findByEstadoTrue();

    /**
     * Obtener todos los tipos de documento inactivos
     * 
     * @return Lista de tipos de documento inactivos
     */
    List<TipoDocumento> findByEstadoFalse();

    /**
     * Obtener todos los tipos de documento ordenados por nombre alfabéticamente
     * 
     * @return Lista de tipos de documento ordenados por nombre
     */
    List<TipoDocumento> findAllByOrderByNombreAsc();

    /**
     * Obtener todos los tipos de documento activos ordenados por nombre alfabéticamente
     * 
     * @return Lista de tipos de documento activos ordenados por nombre
     */
    List<TipoDocumento> findByEstadoTrueOrderByNombreAsc();

    /**
     * Buscar tipos de documento por nombre que contenga el texto especificado
     * 
     * @param nombre Texto a buscar en el nombre
     * @return Lista de tipos de documento que coinciden
     */
    List<TipoDocumento> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Buscar tipos de documento activos por nombre que contenga el texto especificado
     * 
     * @param nombre Texto a buscar en el nombre
     * @return Lista de tipos de documento activos que coinciden
     */
    List<TipoDocumento> findByEstadoTrueAndNombreContainingIgnoreCase(String nombre);

    /**
     * Verificar si existe un tipo de documento con el código especificado
     * 
     * @param codigo Código a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodigo(String codigo);

    /**
     * Verificar si existe un tipo de documento con el código especificado (ignorando mayúsculas/minúsculas)
     * 
     * @param codigo Código a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByCodigoIgnoreCase(String codigo);

    /**
     * Verificar si existe un tipo de documento con el nombre especificado
     * 
     * @param nombre Nombre a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);

    /**
     * Verificar si existe un tipo de documento con el nombre especificado (ignorando mayúsculas/minúsculas)
     * 
     * @param nombre Nombre a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Contar tipos de documento activos
     * 
     * @return Número de tipos de documento activos
     */
    long countByEstadoTrue();

    /**
     * Contar tipos de documento inactivos
     * 
     * @return Número de tipos de documento inactivos
     */
    long countByEstadoFalse();

    /**
     * Obtener estadísticas de tipos de documento
     * 
     * @return Array con [total, activos, inactivos]
     */
    @Query("SELECT COUNT(t), " +
           "SUM(CASE WHEN t.estado = true THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN t.estado = false THEN 1 ELSE 0 END) " +
           "FROM TipoDocumento t")
    Object[] obtenerEstadisticas();

    /**
     * Buscar tipos de documento por código o nombre
     * 
     * @param termino Término de búsqueda
     * @return Lista de tipos de documento que coinciden
     */
    @Query("SELECT t FROM TipoDocumento t WHERE " +
           "LOWER(t.codigo) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(t.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "ORDER BY t.nombre ASC")
    List<TipoDocumento> buscarPorCodigoONombre(@Param("termino") String termino);

    /**
     * Buscar tipos de documento activos por código o nombre
     * 
     * @param termino Término de búsqueda
     * @return Lista de tipos de documento activos que coinciden
     */
    @Query("SELECT t FROM TipoDocumento t WHERE t.estado = true AND " +
           "(LOWER(t.codigo) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(t.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "ORDER BY t.nombre ASC")
    List<TipoDocumento> buscarActivosPorCodigoONombre(@Param("termino") String termino);
}

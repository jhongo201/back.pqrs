package com.claude.springboot.app.repositories;

import com.claude.springboot.app.entities.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, String> {
    
    /**
     * Buscar municipios activos
     */
    List<Municipio> findByActivoTrue();
    
    /**
     * Buscar municipio por código DANE (solo activos)
     */
    Optional<Municipio> findByCodigoDaneAndActivoTrue(String codigoDane);
    
    /**
     * Buscar municipios por departamento
     */
    List<Municipio> findByCodigoDepartamentoAndActivoTrueOrderByNombre(String codigoDepartamento);
    
    /**
     * Buscar municipios por nombre (búsqueda parcial, case insensitive)
     */
    @Query("SELECT m FROM Municipio m WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND m.activo = true")
    List<Municipio> findByNombreContainingIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);
    
    /**
     * Buscar municipios por categoría
     */
    List<Municipio> findByCategoriaAndActivoTrue(String categoria);
    
    /**
     * Buscar municipios con población mayor a un valor
     */
    @Query("SELECT m FROM Municipio m WHERE m.poblacionEstimada >= :poblacion AND m.activo = true ORDER BY m.poblacionEstimada DESC")
    List<Municipio> findByPoblacionEstimadaGreaterThanEqualAndActivoTrue(@Param("poblacion") Integer poblacion);
    
    /**
     * Buscar municipios con información completa del departamento
     */
    @Query("SELECT m.codigoDane, m.nombre, m.codigoDepartamento, m.categoria, m.poblacionEstimada, " +
           "d.nombre, d.capital, d.region " +
           "FROM Municipio m " +
           "INNER JOIN m.departamento d " +
           "WHERE m.activo = true AND d.activo = true " +
           "ORDER BY d.nombre, m.nombre")
    List<Object[]> findMunicipiosConDepartamento();
    
    /**
     * Buscar municipios por nombre y departamento
     */
    @Query("SELECT m FROM Municipio m " +
           "INNER JOIN m.departamento d " +
           "WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombreMunicipio, '%')) " +
           "AND LOWER(d.nombre) LIKE LOWER(CONCAT('%', :nombreDepartamento, '%')) " +
           "AND m.activo = true AND d.activo = true")
    List<Municipio> findByNombreAndDepartamento(@Param("nombreMunicipio") String nombreMunicipio, 
                                               @Param("nombreDepartamento") String nombreDepartamento);
    
    /**
     * Obtener todas las categorías distintas
     */
    @Query("SELECT DISTINCT m.categoria FROM Municipio m WHERE m.categoria IS NOT NULL AND m.activo = true ORDER BY m.categoria")
    List<String> findDistinctCategorias();
    
    /**
     * Contar municipios por departamento
     */
    @Query("SELECT m.codigoDepartamento, COUNT(m) FROM Municipio m WHERE m.activo = true GROUP BY m.codigoDepartamento")
    List<Object[]> countMunicipiosByDepartamento();
    
    /**
     * Contar municipios por categoría
     */
    @Query("SELECT m.categoria, COUNT(m) FROM Municipio m WHERE m.activo = true GROUP BY m.categoria")
    List<Object[]> countMunicipiosByCategoria();
    
    /**
     * Obtener municipios más poblados (top N)
     */
    @Query("SELECT m FROM Municipio m WHERE m.poblacionEstimada IS NOT NULL AND m.activo = true ORDER BY m.poblacionEstimada DESC")
    List<Municipio> findTopMunicipiosByPoblacion();
    
    /**
     * Verificar si existe un municipio por código DANE
     */
    boolean existsByCodigoDaneAndActivoTrue(String codigoDane);
    
    /**
     * Buscar municipios ordenados por nombre
     */
    List<Municipio> findByActivoTrueOrderByNombre();
    
    /**
     * Buscar municipios por región (a través del departamento)
     */
    @Query("SELECT m FROM Municipio m " +
           "INNER JOIN m.departamento d " +
           "WHERE d.region = :region AND m.activo = true AND d.activo = true " +
           "ORDER BY d.nombre, m.nombre")
    List<Municipio> findByRegion(@Param("region") String region);
    
    /**
     * Validar que el código DANE del municipio corresponda al departamento
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM Municipio m " +
           "WHERE m.codigoDane = :codigoMunicipio " +
           "AND m.codigoDepartamento = :codigoDepartamento " +
           "AND m.codigoDane LIKE CONCAT(m.codigoDepartamento, '%') " +
           "AND m.activo = true")
    boolean validarCodigoDaneMunicipio(@Param("codigoMunicipio") String codigoMunicipio, 
                                      @Param("codigoDepartamento") String codigoDepartamento);
}

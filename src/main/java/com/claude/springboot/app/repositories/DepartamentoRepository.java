package com.claude.springboot.app.repositories;

import com.claude.springboot.app.entities.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, String> {
    
    /**
     * Buscar departamentos activos
     */
    List<Departamento> findByActivoTrue();
    
    /**
     * Buscar departamento por código DANE (solo activos)
     */
    Optional<Departamento> findByCodigoDaneAndActivoTrue(String codigoDane);
    
    /**
     * Buscar departamentos por nombre (búsqueda parcial, case insensitive)
     */
    @Query("SELECT d FROM Departamento d WHERE LOWER(d.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND d.activo = true")
    List<Departamento> findByNombreContainingIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);
    
    /**
     * Buscar departamentos por región
     */
    List<Departamento> findByRegionAndActivoTrue(String region);
    
    /**
     * Buscar departamentos por capital
     */
    @Query("SELECT d FROM Departamento d WHERE LOWER(d.capital) LIKE LOWER(CONCAT('%', :capital, '%')) AND d.activo = true")
    List<Departamento> findByCapitalContainingIgnoreCaseAndActivoTrue(@Param("capital") String capital);
    
    /**
     * Obtener todas las regiones distintas
     */
    @Query("SELECT DISTINCT d.region FROM Departamento d WHERE d.region IS NOT NULL AND d.activo = true ORDER BY d.region")
    List<String> findDistinctRegiones();
    
    /**
     * Contar departamentos por región
     */
    @Query("SELECT d.region, COUNT(d) FROM Departamento d WHERE d.activo = true GROUP BY d.region")
    List<Object[]> countDepartamentosByRegion();
    
    /**
     * Obtener estadísticas de departamentos con sus municipios
     */
    @Query("SELECT d.codigoDane, d.nombre, d.region, " +
           "COUNT(m.codigoDane) as totalMunicipios, " +
           "COALESCE(SUM(m.poblacionEstimada), 0) as poblacionTotal " +
           "FROM Departamento d " +
           "LEFT JOIN d.municipios m ON m.activo = true " +
           "WHERE d.activo = true " +
           "GROUP BY d.codigoDane, d.nombre, d.region " +
           "ORDER BY d.nombre")
    List<Object[]> findEstadisticasDepartamentos();
    
    /**
     * Verificar si existe un departamento por código DANE
     */
    boolean existsByCodigoDaneAndActivoTrue(String codigoDane);
    
    /**
     * Buscar departamentos ordenados por nombre
     */
    List<Departamento> findByActivoTrueOrderByNombre();
    
    /**
     * Buscar departamentos con al menos un municipio
     */
    @Query("SELECT DISTINCT d FROM Departamento d " +
           "INNER JOIN d.municipios m " +
           "WHERE d.activo = true AND m.activo = true " +
           "ORDER BY d.nombre")
    List<Departamento> findDepartamentosConMunicipios();
}

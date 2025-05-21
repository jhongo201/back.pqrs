package com.claude.springboot.app.security.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.Area;
import com.claude.springboot.app.security.entities.Direccion;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    boolean existsByNombre(String nombre);

    boolean existsByNombreAndDireccion(String nombre, Direccion direccion);

    @Query("SELECT a FROM Area a LEFT JOIN FETCH a.direccion d LEFT JOIN FETCH d.territorial t")
    List<Area> findAllWithDireccion();

    @Query("SELECT a FROM Area a LEFT JOIN FETCH a.personas LEFT JOIN FETCH a.temasPqrs WHERE a.idArea = :id")
    Optional<Area> findByIdWithDependencies(@Param("id") Long id);

    @Query("SELECT COUNT(p) > 0 FROM Persona p WHERE p.area.idArea = :areaId")
    boolean existsPersonasByAreaId(@Param("areaId") Long areaId);
    
    @Query("SELECT COUNT(t) > 0 FROM TemasPqrs t WHERE t.area.idArea = :areaId")
    boolean existsTemasPqrsByAreaId(@Param("areaId") Long areaId);

}

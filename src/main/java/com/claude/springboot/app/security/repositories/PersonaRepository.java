package com.claude.springboot.app.security.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.Persona;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    
    Optional<Persona> findByEmail(String email);
    
    Optional<Persona> findByNumeroDocumento(String numeroDocumento);
    
    @Query("SELECT p FROM Persona p WHERE p.area.idArea = :areaId AND p.estado = true")
    List<Persona> findAllByAreaId(@Param("areaId") Long areaId);
    
    @Query("SELECT p FROM Persona p WHERE p.empresa.idEmpresa = :empresaId AND p.estado = true")
    List<Persona> findAllByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Persona p WHERE p.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Persona p WHERE p.numeroDocumento = :numeroDocumento")
    boolean existsByNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);
}

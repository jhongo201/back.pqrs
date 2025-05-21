package com.claude.springboot.app.security.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.Ruta;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
    List<Ruta> findByEstadoTrueAndEsPublicaTrue();
    
    // Método existente que usas para permisos
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Ruta r " +
           "WHERE r.ruta = :ruta")
    boolean existsByRuta(@Param("ruta") String ruta);
}

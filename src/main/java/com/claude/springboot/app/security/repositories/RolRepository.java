package com.claude.springboot.app.security.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.Rol;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    Optional<Rol> findByNombre(String nombre);
    
    @Query("SELECT r FROM Rol r WHERE r.estado = true")
    List<Rol> findAllActivos();
    
    @Query("SELECT r FROM Rol r LEFT JOIN FETCH r.permisos WHERE r.idRol = :id")
    Optional<Rol> findByIdWithPermisos(Long id);

    boolean existsByNombre(String nombre);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.rol.idRol = :rolId")
    boolean existsUsuariosByRolId(@Param("rolId") Long rolId);
}

package com.claude.springboot.app.security.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    boolean existsByNombre(String nombre);
    
    boolean existsByNit(String nit);
    
    @Query("SELECT e FROM Empresa e WHERE e.estado = true")
    List<Empresa> findAllActive();

    //@Query("SELECT e FROM Empresa e " +
    //       "LEFT JOIN FETCH e.areas " +
     //      "WHERE e.idEmpresa = :id")
    //Optional<Empresa> findByIdWithAreas(@Param("id") Long id);
}

package com.claude.springboot.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.entities.HistorialAsignacion;
import com.claude.springboot.app.entities.Pqrs;

@Repository
public interface HistorialAsignacionRepository extends JpaRepository<HistorialAsignacion, Long> {
    List<HistorialAsignacion> findByPqrsOrderByFechaAsignacionDesc(Pqrs pqrs);
    List<HistorialAsignacion> findByPqrsIdPqrsOrderByFechaAsignacionDesc(Long idPqrs);
    
}

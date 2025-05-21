package com.claude.springboot.app.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.dto.HistorialAsignacionDTO;
import com.claude.springboot.app.entities.HistorialAsignacion;
import com.claude.springboot.app.entities.Pqrs;
import com.claude.springboot.app.repositories.HistorialAsignacionRepository;
import com.claude.springboot.app.security.entities.Usuario;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HistorialAsignacionServiceImpl implements HistorialAsignacionService {
    
    private final HistorialAsignacionRepository historialRepository;
    
    public HistorialAsignacionServiceImpl(HistorialAsignacionRepository historialRepository) {
        this.historialRepository = historialRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HistorialAsignacionDTO> obtenerHistorialPorPqrs(Long idPqrs) {
        if (idPqrs == null) {
            throw new IllegalArgumentException("El ID de PQRS no puede ser nulo");
        }

        try {
            List<HistorialAsignacion> historiales = historialRepository.findByPqrsIdPqrsOrderByFechaAsignacionDesc(idPqrs);
            return historiales.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al obtener historial de asignaciones para PQRS ID {}: {}", idPqrs, e.getMessage());
            throw new RuntimeException("Error al obtener historial de asignaciones", e);
        }
    }

    private HistorialAsignacionDTO convertirADTO(HistorialAsignacion historial) {
        // Manejo seguro para usuario anterior
        String nombreUsuarioAnterior = "Sin asignar";
        String areaAnterior = "Sin área";
        if (historial.getUsuarioAnterior() != null) {
            if (historial.getUsuarioAnterior().getPersona() != null) {
                nombreUsuarioAnterior = historial.getUsuarioAnterior().getPersona().getNombreCompleto();
                // Asumiendo que tienes un método para obtener el área del usuario
                areaAnterior = historial.getUsuarioAnterior().getPersona().getArea() != null ? 
                    historial.getUsuarioAnterior().getPersona().getArea().getNombre() : "Sin área";
            } else {
                nombreUsuarioAnterior = historial.getUsuarioAnterior().getUsername();
            }
        }

        // Manejo seguro para usuario nuevo
        String nombreUsuarioNuevo = "Sin asignar";
        String areaNueva = "Sin área";
        if (historial.getUsuarioNuevo() != null) {
            if (historial.getUsuarioNuevo().getPersona() != null) {
                nombreUsuarioNuevo = historial.getUsuarioNuevo().getPersona().getNombreCompleto();
                // Asumiendo que tienes un método para obtener el área del usuario
                areaNueva = historial.getUsuarioNuevo().getPersona().getArea() != null ? 
                    historial.getUsuarioNuevo().getPersona().getArea().getNombre() : "Sin área";
            } else {
                nombreUsuarioNuevo = historial.getUsuarioNuevo().getUsername();
            }
        }

        return new HistorialAsignacionDTO(
            historial.getIdHistorial(),
            nombreUsuarioAnterior,
            nombreUsuarioNuevo,
            areaAnterior,
            areaNueva,
            historial.getMotivoCambio(),
            historial.getFechaAsignacion()
        );
    }
}
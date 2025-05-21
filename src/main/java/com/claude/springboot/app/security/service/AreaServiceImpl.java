package com.claude.springboot.app.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.dto.AreaDTO;
import com.claude.springboot.app.security.entities.Area;
import com.claude.springboot.app.security.entities.Direccion;
import com.claude.springboot.app.security.repositories.AreaRepository;
import com.claude.springboot.app.security.repositories.DireccionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final DireccionRepository direccionRepository;
    

    @Override
    @Transactional(readOnly = true)
    public List<AreaDTO> listarTodos() {
        return areaRepository.findAllWithDireccion().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AreaDTO obtenerPorId(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área no encontrada"));
        return convertirADTO(area);
    }

    @Override
    @Transactional
    public AreaDTO crear(AreaDTO areaDTO) {
        Direccion direccion = direccionRepository.findById(areaDTO.getIdDireccion())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        if (areaRepository.existsByNombreAndDireccion(areaDTO.getNombre(), direccion)) {
            throw new RuntimeException("Ya existe un área con este nombre en esta dirección");
        }

        Area area = new Area();
        area.setDireccion(direccion);
        area.setNombre(areaDTO.getNombre());
        area.setDescripcion(areaDTO.getDescripcion());

        return convertirADTO(areaRepository.save(area));
    }

    @Override
    @Transactional
    public AreaDTO actualizar(Long id, AreaDTO areaDTO) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área no encontrada"));

        if (!area.getNombre().equals(areaDTO.getNombre()) &&
                areaRepository.existsByNombreAndDireccion(areaDTO.getNombre(), area.getDireccion())) {
            throw new RuntimeException("Ya existe un área con este nombre en esta dirección");
        }

        area.setNombre(areaDTO.getNombre());
        area.setDescripcion(areaDTO.getDescripcion());
        area.setEstado(areaDTO.isEstado());

        return convertirADTO(areaRepository.save(area));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        try {
            Area area = areaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Área no encontrada con ID: " + id));
    
            // Verificar si tiene dependencias usando consultas separadas
        boolean tienePersonas = areaRepository.existsPersonasByAreaId(id);
        boolean tieneTemasPqrs = areaRepository.existsTemasPqrsByAreaId(id);

        if (tienePersonas || tieneTemasPqrs) {
            area.setEstado(false);
            areaRepository.save(area);
        } else {
            areaRepository.delete(area);
        }
        } catch (Exception e) {
            log.error("Error al eliminar área: {}", id, e);
            throw new RuntimeException("Error al eliminar el área: " + e.getMessage());
        }
    }

    private AreaDTO convertirADTO(Area area) {
        AreaDTO dto = new AreaDTO();
        dto.setIdArea(area.getIdArea());
        dto.setIdDireccion(area.getDireccion() != null ? area.getDireccion().getIdDireccion() : null);
        dto.setNombre(area.getNombre());
        dto.setDescripcion(area.getDescripcion());
        dto.setEstado(area.isEstado());
        return dto;
    }
}

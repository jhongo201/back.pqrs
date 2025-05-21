package com.claude.springboot.app.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.dto.ModuloDTO;
import com.claude.springboot.app.security.entities.Modulo;
import com.claude.springboot.app.security.repositories.ModuloRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuloServiceImpl implements ModuloService {

    private final ModuloRepository moduloRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ModuloDTO> listarTodos() {
        try {
            return moduloRepository.findAll().stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar módulos", e);
            throw new RuntimeException("Error al obtener la lista de módulos");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ModuloDTO obtenerPorId(Long id) {
        try {
            Modulo modulo = moduloRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + id));
            return convertirADTO(modulo);
        } catch (Exception e) {
            log.error("Error al obtener módulo con ID: {}", id, e);
            throw new RuntimeException("Error al obtener el módulo");
        }
    }

    @Override
    @Transactional
    public ModuloDTO crear(ModuloDTO moduloDTO) {
        try {
            // Verificar si ya existe un módulo con el mismo nombre
            if (moduloRepository.existsByNombre(moduloDTO.getNombre())) {
                throw new RuntimeException("Ya existe un módulo con el nombre: " + moduloDTO.getNombre());
            }

            Modulo modulo = new Modulo();
            modulo.setNombre(moduloDTO.getNombre());
            modulo.setDescripcion(moduloDTO.getDescripcion());
            modulo.setEstado(true);

            modulo = moduloRepository.save(modulo);
            return convertirADTO(modulo);
        } catch (Exception e) {
            log.error("Error al crear módulo", e);
            throw new RuntimeException("Error al crear el módulo");
        }
    }

    @Override
    @Transactional
    public ModuloDTO actualizar(Long id, ModuloDTO moduloDTO) {
        try {
            Modulo modulo = moduloRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + id));

            // Verificar nombre duplicado solo si está cambiando el nombre
            if (!modulo.getNombre().equals(moduloDTO.getNombre()) && 
                moduloRepository.existsByNombre(moduloDTO.getNombre())) {
                throw new RuntimeException("Ya existe un módulo con el nombre: " + moduloDTO.getNombre());
            }

            modulo.setNombre(moduloDTO.getNombre());
            modulo.setDescripcion(moduloDTO.getDescripcion());
            modulo.setEstado(moduloDTO.isEstado());

            modulo = moduloRepository.save(modulo);
            return convertirADTO(modulo);
        } catch (Exception e) {
            log.error("Error al actualizar módulo con ID: {}", id, e);
            throw new RuntimeException("Error al actualizar el módulo");
        }
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        try {
            Modulo modulo = moduloRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + id));
            
            // Verificar si tiene rutas asociadas
            if (!modulo.getRutas().isEmpty()) {
                modulo.setEstado(false);
                moduloRepository.save(modulo);
            } else {
                moduloRepository.delete(modulo);
            }
        } catch (Exception e) {
            log.error("Error al eliminar módulo con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar el módulo");
        }
    }

    private ModuloDTO convertirADTO(Modulo modulo) {
        ModuloDTO dto = new ModuloDTO();
        dto.setIdModulo(modulo.getIdModulo());
        dto.setNombre(modulo.getNombre());
        dto.setDescripcion(modulo.getDescripcion());
        dto.setEstado(modulo.isEstado());
        return dto;
    }
}

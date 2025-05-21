package com.claude.springboot.app.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.dto.RolDTO;
import com.claude.springboot.app.security.entities.Rol;
import com.claude.springboot.app.security.repositories.RolRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listarTodos() {
        try {
            return rolRepository.findAll().stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar roles", e);
            throw new RuntimeException("Error al obtener la lista de roles");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO obtenerPorId(Long id) {
        try {
            Rol rol = rolRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
            return convertirADTO(rol);
        } catch (Exception e) {
            log.error("Error al obtener rol con ID: {}", id, e);
            throw new RuntimeException("Error al obtener el rol");
        }
    }

    @Override
    @Transactional
    public RolDTO crear(RolDTO rolDTO) {
        try {
            // Verificar si ya existe un rol con el mismo nombre
            if (rolRepository.existsByNombre(rolDTO.getNombre())) {
                throw new RuntimeException("Ya existe un rol con el nombre: " + rolDTO.getNombre());
            }

            Rol rol = new Rol();
            rol.setNombre(rolDTO.getNombre());
            rol.setDescripcion(rolDTO.getDescripcion());
            rol.setEstado(true);

            rol = rolRepository.save(rol);
            return convertirADTO(rol);
        } catch (Exception e) {
            log.error("Error al crear rol", e);
            throw new RuntimeException("Error al crear el rol");
        }
    }

    @Override
    @Transactional
    public RolDTO actualizar(Long id, RolDTO rolDTO) {
        try {
            Rol rol = rolRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

            // Verificar nombre duplicado solo si está cambiando el nombre
            if (!rol.getNombre().equals(rolDTO.getNombre()) && 
                rolRepository.existsByNombre(rolDTO.getNombre())) {
                throw new RuntimeException("Ya existe un rol con el nombre: " + rolDTO.getNombre());
            }

            rol.setNombre(rolDTO.getNombre());
            rol.setDescripcion(rolDTO.getDescripcion());
            rol.setEstado(rolDTO.isEstado());

            rol = rolRepository.save(rol);
            return convertirADTO(rol);
        } catch (Exception e) {
            log.error("Error al actualizar rol con ID: {}", id, e);
            throw new RuntimeException("Error al actualizar el rol");
        }
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        try {
            Rol rol = rolRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
            
            // Verificar si tiene usuarios asociados
            if (rolRepository.existsUsuariosByRolId(id)) {
                rol.setEstado(false);
                rolRepository.save(rol);
            } else {
                rolRepository.delete(rol);
            }
        } catch (Exception e) {
            log.error("Error al eliminar rol con ID: {}", id, e);
            throw new RuntimeException("Error al eliminar el rol");
        }
    }

    private RolDTO convertirADTO(Rol rol) {
        RolDTO dto = new RolDTO();
        dto.setIdRol(rol.getIdRol());
        dto.setNombre(rol.getNombre());
        dto.setDescripcion(rol.getDescripcion());
        dto.setEstado(rol.isEstado());
        return dto;
    }
}

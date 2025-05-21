package com.claude.springboot.app.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.dto.AsignacionPermisosDTO;
import com.claude.springboot.app.security.dto.PermisoRolDTO;
import com.claude.springboot.app.security.dto.PermisoRutaDTO;
import com.claude.springboot.app.security.entities.PermisoRol;
import com.claude.springboot.app.security.entities.Rol;
import com.claude.springboot.app.security.entities.Ruta;
import com.claude.springboot.app.security.repositories.PermisoRolRepository;
import com.claude.springboot.app.security.repositories.RolRepository;
import com.claude.springboot.app.security.repositories.RutaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermisoRolServiceImpl implements PermisoRolService {

    private final PermisoRolRepository permisoRolRepository;
    private final RolRepository rolRepository;
    private final RutaRepository rutaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PermisoRolDTO> listarPermisosPorRol(Long idRol) {
        try {
            return permisoRolRepository.findByRolIdRol(idRol).stream()
                    .map(this::convertirADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar permisos del rol: {}", idRol, e);
            throw new RuntimeException("Error al obtener los permisos del rol");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PermisoRolDTO obtenerPermiso(Long idPermiso) {
        try {
            PermisoRol permiso = permisoRolRepository.findById(idPermiso)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));
            return convertirADTO(permiso);
        } catch (Exception e) {
            log.error("Error al obtener permiso: {}", idPermiso, e);
            throw new RuntimeException("Error al obtener el permiso");
        }
    }

    @Override
    @Transactional
    public void asignarPermisosARol(AsignacionPermisosDTO asignacionDTO) {
        try {
            Rol rol = rolRepository.findById(asignacionDTO.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            // Desactivar permisos anteriores
            permisoRolRepository.desactivarPermisosPorRol(rol.getIdRol());

            // Crear nuevos permisos
            for (PermisoRutaDTO permisoDTO : asignacionDTO.getPermisos()) {
                Ruta ruta = rutaRepository.findById(permisoDTO.getIdRuta())
                        .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

                PermisoRol permiso = new PermisoRol();
                permiso.setRol(rol);
                permiso.setRuta(ruta);
                permiso.setPuedeLeer(permisoDTO.isPuedeLeer());
                permiso.setPuedeEscribir(permisoDTO.isPuedeEscribir());
                permiso.setPuedeActualizar(permisoDTO.isPuedeActualizar());
                permiso.setPuedeEliminar(permisoDTO.isPuedeEliminar());
                permiso.setEstado(true);

                permisoRolRepository.save(permiso);
            }
        } catch (Exception e) {
            log.error("Error al asignar permisos al rol: {}", asignacionDTO.getIdRol(), e);
            throw new RuntimeException("Error al asignar permisos al rol");
        }
    }

    @Override
    @Transactional
    public PermisoRolDTO actualizarPermiso(Long idPermiso, PermisoRolDTO permisoDTO) {
        try {
            PermisoRol permiso = permisoRolRepository.findById(idPermiso)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

            permiso.setPuedeLeer(permisoDTO.isPuedeLeer());
            permiso.setPuedeEscribir(permisoDTO.isPuedeEscribir());
            permiso.setPuedeActualizar(permisoDTO.isPuedeActualizar());
            permiso.setPuedeEliminar(permisoDTO.isPuedeEliminar());
            permiso.setEstado(permisoDTO.isEstado());

            permiso = permisoRolRepository.save(permiso);
            return convertirADTO(permiso);
        } catch (Exception e) {
            log.error("Error al actualizar permiso: {}", idPermiso, e);
            throw new RuntimeException("Error al actualizar el permiso");
        }
    }

    @Override
    @Transactional
    public void eliminarPermiso(Long idPermiso) {
        try {
            PermisoRol permiso = permisoRolRepository.findById(idPermiso)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));
            permiso.setEstado(false);
            permisoRolRepository.save(permiso);
        } catch (Exception e) {
            log.error("Error al eliminar permiso: {}", idPermiso, e);
            throw new RuntimeException("Error al eliminar el permiso");
        }
    }

    private PermisoRolDTO convertirADTO(PermisoRol permiso) {
        PermisoRolDTO dto = new PermisoRolDTO();
        dto.setIdPermiso(permiso.getIdPermiso());
        dto.setIdRol(permiso.getRol().getIdRol());
        dto.setIdRuta(permiso.getRuta().getIdRuta());
        dto.setPuedeLeer(permiso.isPuedeLeer());
        dto.setPuedeEscribir(permiso.isPuedeEscribir());
        dto.setPuedeActualizar(permiso.isPuedeActualizar());
        dto.setPuedeEliminar(permiso.isPuedeEliminar());
        dto.setEstado(permiso.isEstado());
        return dto;
    }
}

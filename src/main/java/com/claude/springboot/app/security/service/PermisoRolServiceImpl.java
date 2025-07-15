package com.claude.springboot.app.security.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                    .filter(permiso -> permiso.getRuta() != null) // Filtrar registros con ruta null
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

            // Obtener todos los permisos actuales del rol
            List<PermisoRol> permisosActuales = permisoRolRepository.findByRolIdRol(rol.getIdRol());
            
            // Crear un mapa para acceso rápido por ID de ruta
            Map<Long, PermisoRol> mapaPermisosExistentes = new HashMap<>();
            for (PermisoRol p : permisosActuales) {
                mapaPermisosExistentes.put(p.getRuta().getIdRuta(), p);
            }
            
            // Conjunto para rastrear las rutas que se están actualizando
            Set<Long> rutasActualizadas = new HashSet<>();

            // Actualizar o crear permisos
            for (PermisoRutaDTO permisoDTO : asignacionDTO.getPermisos()) {
                Ruta ruta = rutaRepository.findById(permisoDTO.getIdRuta())
                        .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
                
                PermisoRol permiso;
                
                // Verificar si ya existe un permiso para esta ruta
                if (mapaPermisosExistentes.containsKey(ruta.getIdRuta())) {
                    // Actualizar permiso existente
                    permiso = mapaPermisosExistentes.get(ruta.getIdRuta());
                    permiso.setPuedeLeer(permisoDTO.isPuedeLeer());
                    permiso.setPuedeEscribir(permisoDTO.isPuedeEscribir());
                    permiso.setPuedeActualizar(permisoDTO.isPuedeActualizar());
                    permiso.setPuedeEliminar(permisoDTO.isPuedeEliminar());
                    permiso.setEstado(true); // Asegurarse de que esté activo
                } else {
                    // Crear nuevo permiso
                    permiso = new PermisoRol();
                    permiso.setRol(rol);
                    permiso.setRuta(ruta);
                    permiso.setPuedeLeer(permisoDTO.isPuedeLeer());
                    permiso.setPuedeEscribir(permisoDTO.isPuedeEscribir());
                    permiso.setPuedeActualizar(permisoDTO.isPuedeActualizar());
                    permiso.setPuedeEliminar(permisoDTO.isPuedeEliminar());
                    permiso.setEstado(true);
                }
                
                permisoRolRepository.save(permiso);
                rutasActualizadas.add(ruta.getIdRuta());
            }
            
            // Desactivar permisos que no están en la lista de actualización
            for (PermisoRol permiso : permisosActuales) {
                if (!rutasActualizadas.contains(permiso.getRuta().getIdRuta())) {
                    permiso.setEstado(false);
                    permisoRolRepository.save(permiso);
                }
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
        
        // Validación para evitar NullPointerException
        if (permiso.getRol() != null) {
            dto.setIdRol(permiso.getRol().getIdRol());
        } else {
            log.warn("PermisoRol con ID {} tiene rol null", permiso.getIdPermiso());
        }
        
        if (permiso.getRuta() != null) {
            dto.setIdRuta(permiso.getRuta().getIdRuta());
        } else {
            log.warn("PermisoRol con ID {} tiene ruta null", permiso.getIdPermiso());
        }
        
        dto.setPuedeLeer(permiso.isPuedeLeer());
        dto.setPuedeEscribir(permiso.isPuedeEscribir());
        dto.setPuedeActualizar(permiso.isPuedeActualizar());
        dto.setPuedeEliminar(permiso.isPuedeEliminar());
        dto.setEstado(permiso.isEstado());
        return dto;
    }
}

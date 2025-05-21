package com.claude.springboot.app.security.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.dto.ActualizarRutaDTO;
import com.claude.springboot.app.security.dto.PermisoRolRutaDTO;
import com.claude.springboot.app.security.dto.PermisoRolRutaResponseDTO;
import com.claude.springboot.app.security.dto.RegistroRutaDTO;
import com.claude.springboot.app.security.dto.RutaResponseDTO;
import com.claude.springboot.app.security.entities.Modulo;
import com.claude.springboot.app.security.entities.PermisoRol;
import com.claude.springboot.app.security.entities.Rol;
import com.claude.springboot.app.security.entities.Ruta;
import com.claude.springboot.app.security.repositories.ModuloRepository;
import com.claude.springboot.app.security.repositories.PermisoRolRepository;
import com.claude.springboot.app.security.repositories.RolRepository;
import com.claude.springboot.app.security.repositories.RutaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RutaServiceImpl implements RutaService {

    private final RutaRepository rutaRepository;
    private final ModuloRepository moduloRepository;
    private final RolRepository rolRepository;
    private final PermisoRolRepository permisoRolRepository;

    public List<String> obtenerRutasPublicas() {
        return rutaRepository.findByEstadoTrueAndEsPublicaTrue()
                .stream()
                .map(Ruta::getRuta)
                .collect(Collectors.toList());
    }

    @Transactional
    public RutaResponseDTO registrarRuta(RegistroRutaDTO registroDTO) {
        try {
            log.info("Iniciando registro de ruta: {}", registroDTO.getRuta());

            // Validar si la ruta ya existe
            if (rutaRepository.existsByRuta(registroDTO.getRuta())) {
                throw new IllegalArgumentException(
                        "La ruta " + registroDTO.getRuta() + " ya está registrada en el sistema");
            }

            // Validar que si es pública no tenga permisos
            if (registroDTO.getEsPublica() &&
                    registroDTO.getPermisosRol() != null &&
                    !registroDTO.getPermisosRol().isEmpty()) {
                throw new IllegalArgumentException("Una ruta pública no puede tener permisos de rol asociados");
            }

            // Validar que si es privada tenga al menos un permiso
            if (!registroDTO.getEsPublica() &&
                    (registroDTO.getPermisosRol() == null ||
                            registroDTO.getPermisosRol().isEmpty())) {
                throw new IllegalArgumentException("Una ruta privada debe tener al menos un permiso de rol asociado");
            }

            log.info("Buscando módulo con ID: {}", registroDTO.getIdModulo());
            // Obtener el módulo
            Modulo modulo = moduloRepository.findById(registroDTO.getIdModulo())
                    .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));

            // Validar que el módulo esté activo
            if (!modulo.isEstado()) {
                throw new IllegalArgumentException("El módulo seleccionado no está activo");
            }

            // Crear y guardar la ruta
            Ruta ruta = new Ruta();
            ruta.setModulo(modulo);
            ruta.setRuta(registroDTO.getRuta());
            ruta.setDescripcion(registroDTO.getDescripcion());
            ruta.setEstado(true);
            ruta.setEsPublica(registroDTO.getEsPublica());
            ruta.setFechaCreacion(LocalDateTime.now());

            ruta = rutaRepository.save(ruta);

            // Lista para almacenar los permisos creados
            List<PermisoRol> permisosCreados = new ArrayList<>();

            // Si es privada, crear los permisos
            if (!registroDTO.getEsPublica()) {

                // Validar roles únicos
                Set<Long> rolesUnicos = new HashSet<>();
                for (PermisoRolRutaDTO permisoDTO : registroDTO.getPermisosRol()) {

                    // Verificar si el rol ya está en la lista de permisos
                    if (!rolesUnicos.add(permisoDTO.getIdRol())) {
                        throw new IllegalArgumentException("El rol con ID " + permisoDTO.getIdRol() +
                                " está duplicado en la lista de permisos");
                    }

                    // Obtener y validar el rol
                    Rol rol = rolRepository.findById(permisoDTO.getIdRol())
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

                    // Validar que el rol esté activo
                    if (!rol.isEstado()) {
                        throw new IllegalArgumentException("El rol " + rol.getNombre() + " no está activo");
                    }

                    // Validar si el rol ya tiene permisos para esta ruta
                    if (permisoRolRepository.existsByRolAndRuta(rol, ruta)) {
                        throw new IllegalArgumentException("El rol " + rol.getNombre() +
                                " ya tiene permisos asignados para esta ruta");
                    }

                    PermisoRol permiso = new PermisoRol();
                    permiso.setRol(rol);
                    permiso.setRuta(ruta);
                    permiso.setPuedeLeer(permisoDTO.getPuedeLeer());
                    permiso.setPuedeEscribir(permisoDTO.getPuedeEscribir());
                    permiso.setPuedeEliminar(permisoDTO.getPuedeEliminar());
                    permiso.setPuedeActualizar(permisoDTO.getPuedeActualizar());
                    permiso.setEstado(true);
                    permiso.setFechaCreacion(LocalDateTime.now());

                    permisosCreados.add(permisoRolRepository.save(permiso));
                }
            }

            // Crear y retornar el DTO de respuesta
            RutaResponseDTO response = new RutaResponseDTO();
            response.setIdRuta(ruta.getIdRuta());
            response.setIdModulo(modulo.getIdModulo());
            response.setNombreModulo(modulo.getNombre());
            response.setRuta(ruta.getRuta());
            response.setDescripcion(ruta.getDescripcion());
            response.setEstado(ruta.isEstado());
            response.setEsPublica(ruta.isEsPublica());
            response.setFechaCreacion(ruta.getFechaCreacion());

            if (!ruta.isEsPublica()) {
                response.setPermisos(permisosCreados.stream()
                        .map(permiso -> new PermisoRolRutaResponseDTO(
                                permiso.getIdPermiso(),
                                permiso.getRol().getIdRol(),
                                permiso.getRol().getNombre(),
                                permiso.isPuedeLeer(),
                                permiso.isPuedeEscribir(),
                                permiso.isPuedeEliminar(),
                                permiso.isPuedeActualizar(),
                                permiso.isEstado()))
                        .collect(Collectors.toList()));
            }

            return response;

        } catch (Exception e) {
            log.error("Error al registrar ruta: {}", e.getMessage());
            log.error("Stacktrace completo:", e);
            throw new RuntimeException("Error al registrar ruta: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutaResponseDTO> listarRutas() {
        List<Ruta> rutas = rutaRepository.findAll();
        return rutas.stream()
                .map(this::mapToRutaResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RutaResponseDTO obtenerRuta(Long id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        return mapToRutaResponseDTO(ruta);
    }

    @Override
    @Transactional
    public RutaResponseDTO actualizarRuta(ActualizarRutaDTO actualizarDTO) {
        try {
            log.info("Iniciando actualización de ruta: {}", actualizarDTO.getRuta());
            
            // Validar que la ruta existe
            Ruta ruta = rutaRepository.findById(actualizarDTO.getIdRuta())
                    .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
    
            // Validar si la ruta ya existe (solo si se está cambiando la ruta)
            if (!ruta.getRuta().equals(actualizarDTO.getRuta()) && 
                rutaRepository.existsByRuta(actualizarDTO.getRuta())) {
                throw new IllegalArgumentException("La ruta " + actualizarDTO.getRuta() + " ya está registrada en el sistema");
            }
            
            // Validar que si es pública no tenga permisos
            if (actualizarDTO.getEsPublica() && 
                actualizarDTO.getPermisosRol() != null && 
                !actualizarDTO.getPermisosRol().isEmpty()) {
                throw new IllegalArgumentException("Una ruta pública no puede tener permisos de rol asociados");
            }
            
            // Validar que si es privada tenga al menos un permiso
            if (!actualizarDTO.getEsPublica() && 
                (actualizarDTO.getPermisosRol() == null || 
                 actualizarDTO.getPermisosRol().isEmpty())) {
                throw new IllegalArgumentException("Una ruta privada debe tener al menos un permiso de rol asociado");
            }
    
            // Obtener y validar el módulo
            Modulo modulo = moduloRepository.findById(actualizarDTO.getIdModulo())
                    .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
            
            // Validar que el módulo esté activo
            if (!modulo.isEstado()) {
                throw new IllegalArgumentException("El módulo seleccionado no está activo");
            }
    
            // Actualizar la ruta
            ruta.setModulo(modulo);
            ruta.setRuta(actualizarDTO.getRuta());
            ruta.setDescripcion(actualizarDTO.getDescripcion());
            ruta.setEstado(actualizarDTO.getEstado());
            ruta.setEsPublica(actualizarDTO.getEsPublica());
            
            ruta = rutaRepository.save(ruta);
            
            // Eliminar permisos existentes
            permisoRolRepository.deleteByRuta(ruta);
            
            List<PermisoRol> permisosCreados = new ArrayList<>();
            
            // Si es privada, crear nuevos permisos
            if (!actualizarDTO.getEsPublica() && actualizarDTO.getPermisosRol() != null) {
                // Validar roles únicos
                Set<Long> rolesUnicos = new HashSet<>();
                
                for (PermisoRolRutaDTO permisoDTO : actualizarDTO.getPermisosRol()) {
                    // Verificar si el rol ya está en la lista de permisos
                    if (!rolesUnicos.add(permisoDTO.getIdRol())) {
                        throw new IllegalArgumentException("El rol con ID " + permisoDTO.getIdRol() + 
                            " está duplicado en la lista de permisos");
                    }
    
                    // Obtener y validar el rol
                    Rol rol = rolRepository.findById(permisoDTO.getIdRol())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + permisoDTO.getIdRol()));
    
                    // Validar que el rol esté activo
                    if (!rol.isEstado()) {
                        throw new IllegalArgumentException("El rol " + rol.getNombre() + " no está activo");
                    }
    
                    PermisoRol permiso = new PermisoRol();
                    permiso.setRol(rol);
                    permiso.setRuta(ruta);
                    permiso.setPuedeLeer(permisoDTO.getPuedeLeer());
                    permiso.setPuedeEscribir(permisoDTO.getPuedeEscribir());
                    permiso.setPuedeEliminar(permisoDTO.getPuedeEliminar());
                    permiso.setPuedeActualizar(permisoDTO.getPuedeActualizar());
                    permiso.setEstado(true);
                    permiso.setFechaCreacion(LocalDateTime.now());
                    
                    permisosCreados.add(permisoRolRepository.save(permiso));
                }
            }
            
            // Crear el DTO de respuesta
            RutaResponseDTO response = new RutaResponseDTO();
            response.setIdRuta(ruta.getIdRuta());
            response.setIdModulo(modulo.getIdModulo());
            response.setNombreModulo(modulo.getNombre());
            response.setRuta(ruta.getRuta());
            response.setDescripcion(ruta.getDescripcion());
            response.setEstado(ruta.isEstado());
            response.setEsPublica(ruta.isEsPublica());
            response.setFechaCreacion(ruta.getFechaCreacion());
            
            if (!ruta.isEsPublica()) {
                response.setPermisos(permisosCreados.stream()
                    .map(permiso -> new PermisoRolRutaResponseDTO(
                        permiso.getIdPermiso(),
                        permiso.getRol().getIdRol(),
                        permiso.getRol().getNombre(),
                        permiso.isPuedeLeer(),
                        permiso.isPuedeEscribir(),
                        permiso.isPuedeEliminar(),
                        permiso.isPuedeActualizar(),
                        permiso.isEstado()
                    ))
                    .collect(Collectors.toList()));
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("Error al actualizar ruta: {}", e.getMessage());
            log.error("Stacktrace completo:", e);
            throw new RuntimeException("Error al actualizar ruta: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void eliminarRuta(Long id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        // Eliminar permisos asociados
        permisoRolRepository.deleteByRuta(ruta);

        // Eliminar la ruta
        rutaRepository.delete(ruta);
    }

    public RutaResponseDTO mapToRutaResponseDTO(Ruta ruta) {
        RutaResponseDTO dto = new RutaResponseDTO();
        dto.setIdRuta(ruta.getIdRuta());
        dto.setIdModulo(ruta.getModulo().getIdModulo());
        dto.setNombreModulo(ruta.getModulo().getNombre());
        dto.setRuta(ruta.getRuta());
        dto.setDescripcion(ruta.getDescripcion());
        dto.setEstado(ruta.isEstado());
        dto.setEsPublica(ruta.isEsPublica());
        dto.setFechaCreacion(ruta.getFechaCreacion());

        // Obtener y mapear permisos si la ruta no es pública
        if (!ruta.isEsPublica()) {
            List<PermisoRol> permisos = permisoRolRepository.findByRuta(ruta);
            dto.setPermisos(permisos.stream()
                    .map(this::mapToPermisoRolResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public PermisoRolRutaResponseDTO mapToPermisoRolResponseDTO(PermisoRol permiso) {
        return new PermisoRolRutaResponseDTO(
                permiso.getIdPermiso(),
                permiso.getRol().getIdRol(),
                permiso.getRol().getNombre(),
                permiso.isPuedeLeer(),
                permiso.isPuedeEscribir(),
                permiso.isPuedeEliminar(),
                permiso.isPuedeActualizar(),
                permiso.isEstado());
    }

}

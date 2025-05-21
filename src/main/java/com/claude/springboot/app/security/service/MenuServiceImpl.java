package com.claude.springboot.app.security.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.dto.MenuResponseDTO;
import com.claude.springboot.app.security.dto.ModuloMenuDTO;
import com.claude.springboot.app.security.dto.RutaMenuDTO;
import com.claude.springboot.app.security.entities.Modulo;
import com.claude.springboot.app.security.entities.PermisoRol;
import com.claude.springboot.app.security.entities.Rol;
import com.claude.springboot.app.security.entities.Ruta;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.PermisoRolRepository;
import com.claude.springboot.app.security.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final UsuarioRepository usuarioRepository;
    private final PermisoRolRepository permisoRolRepository;

    @Override
    @Transactional(readOnly = true)
    public MenuResponseDTO getMenuOptionsForUser(String username) {
        try {
            log.info("Obteniendo opciones de menú para el usuario: {}", username);
            
            if (username == null || username.isEmpty()) {
                log.error("Nombre de usuario nulo o vacío");
                return new MenuResponseDTO(new ArrayList<>());
            }
            
            // Obtener el usuario y su rol
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
            
            log.debug("Usuario encontrado: {}", usuario.getIdUsuario());
            
            Rol rol = usuario.getRol();
            if (rol == null) {
                log.warn("El usuario {} no tiene un rol asignado", username);
                return new MenuResponseDTO(new ArrayList<>());
            }
            
            log.debug("Rol del usuario: {}", rol.getNombre());
            
            // Obtener todos los permisos del rol donde puede_leer es true usando carga temprana
            List<PermisoRol> permisos = permisoRolRepository.findPermisosWithModulosAndRutasByRolId(rol.getIdRol());
            log.debug("Permisos encontrados: {}", permisos.size());
            
            if (permisos.isEmpty()) {
                log.warn("No se encontraron permisos para el rol: {}", rol.getNombre());
                return new MenuResponseDTO(new ArrayList<>());
            }
            
            // Agrupar las rutas por módulo
            Map<Modulo, List<Ruta>> modulosConRutas = new HashMap<>();
            
            for (PermisoRol permiso : permisos) {
                if (permiso == null) {
                    log.warn("Se encontró un permiso nulo");
                    continue;
                }
                
                Ruta ruta = permiso.getRuta();
                if (ruta == null) {
                    log.warn("Permiso sin ruta asociada: {}", permiso.getIdPermiso());
                    continue;
                }
                
                if (!ruta.isEstado()) {
                    log.debug("Ruta inactiva: {}", ruta.getRuta());
                    continue;
                }
                
                Modulo modulo = ruta.getModulo();
                if (modulo == null) {
                    log.warn("Ruta sin módulo asociado: {}", ruta.getRuta());
                    continue;
                }
                
                if (!modulo.isEstado()) {
                    log.debug("Módulo inactivo: {}", modulo.getNombre());
                    continue;
                }
                
                if (!modulosConRutas.containsKey(modulo)) {
                    modulosConRutas.put(modulo, new ArrayList<>());
                }
                modulosConRutas.get(modulo).add(ruta);
            }
            
            log.debug("Módulos con rutas: {}", modulosConRutas.size());
            
            if (modulosConRutas.isEmpty()) {
                log.warn("No se encontraron módulos activos con rutas activas para el usuario: {}", username);
                return new MenuResponseDTO(new ArrayList<>());
            }
            
            // Convertir a DTOs
            List<ModuloMenuDTO> modulosDTO = new ArrayList<>();
            
            for (Map.Entry<Modulo, List<Ruta>> entry : modulosConRutas.entrySet()) {
                Modulo modulo = entry.getKey();
                List<Ruta> rutas = entry.getValue();
                
                if (rutas.isEmpty()) {
                    log.debug("Módulo sin rutas: {}", modulo.getNombre());
                    continue;
                }
                
                // Convertir rutas a DTOs
                List<RutaMenuDTO> rutasDTO = new ArrayList<>();
                
                for (Ruta ruta : rutas) {
                    // Buscar el permiso específico para esta ruta
                    PermisoRol permiso = null;
                    for (PermisoRol p : permisos) {
                        if (p.getRuta() != null && p.getRuta().getIdRuta().equals(ruta.getIdRuta())) {
                            permiso = p;
                            break;
                        }
                    }
                    
                    RutaMenuDTO rutaDTO = RutaMenuDTO.builder()
                            .id(ruta.getIdRuta())
                            .ruta(ruta.getRuta())
                            .descripcion(ruta.getDescripcion())
                            .puedeLeer(permiso != null && permiso.isPuedeLeer())
                            .puedeEscribir(permiso != null && permiso.isPuedeEscribir())
                            .puedeActualizar(permiso != null && permiso.isPuedeActualizar())
                            .puedeEliminar(permiso != null && permiso.isPuedeEliminar())
                            .build();
                    
                    rutasDTO.add(rutaDTO);
                }
                
                if (rutasDTO.isEmpty()) {
                    log.debug("No se encontraron rutas con permisos para el módulo: {}", modulo.getNombre());
                    continue;
                }
                
                ModuloMenuDTO moduloDTO = ModuloMenuDTO.builder()
                        .id(modulo.getIdModulo())
                        .nombre(modulo.getNombre())
                        .descripcion(modulo.getDescripcion())
                        .rutas(rutasDTO)
                        .build();
                
                modulosDTO.add(moduloDTO);
            }
            
            log.info("Opciones de menú generadas correctamente para el usuario: {}, módulos: {}", username, modulosDTO.size());
            return new MenuResponseDTO(modulosDTO);
            
        } catch (Exception e) {
            log.error("Error al obtener opciones de menú para el usuario: {}", username, e);
            // En lugar de propagar la excepción, devolvemos un menú vacío
            return new MenuResponseDTO(new ArrayList<>());
        }
    }
}

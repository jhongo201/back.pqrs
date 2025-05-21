package com.claude.springboot.app.security.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        log.info("Obteniendo opciones de menú para el usuario: {}", username);
        
        // Obtener el usuario y su rol
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Rol rol = usuario.getRol();
        if (rol == null) {
            log.warn("El usuario {} no tiene un rol asignado", username);
            return new MenuResponseDTO(new ArrayList<>());
        }
        
        // Obtener todos los permisos del rol donde puede_leer es true
        List<PermisoRol> permisos = permisoRolRepository.findByRolAndPuedeLeerTrue(rol);
        
        // Agrupar las rutas por módulo
        Map<Modulo, List<Ruta>> modulosConRutas = new HashMap<>();
        
        for (PermisoRol permiso : permisos) {
            Ruta ruta = permiso.getRuta();
            if (ruta != null && ruta.isEstado()) {  // Solo incluir rutas activas
                Modulo modulo = ruta.getModulo();
                if (modulo != null && modulo.isEstado()) {  // Solo incluir módulos activos
                    if (!modulosConRutas.containsKey(modulo)) {
                        modulosConRutas.put(modulo, new ArrayList<>());
                    }
                    modulosConRutas.get(modulo).add(ruta);
                }
            }
        }
        
        // Convertir a DTOs
        List<ModuloMenuDTO> modulosDTO = modulosConRutas.entrySet().stream()
                .map(entry -> {
                    Modulo modulo = entry.getKey();
                    List<Ruta> rutas = entry.getValue();
                    
                    // Convertir rutas a DTOs
                    List<RutaMenuDTO> rutasDTO = rutas.stream()
                            .map(ruta -> {
                                // Buscar el permiso específico para esta ruta
                                PermisoRol permiso = permisos.stream()
                                        .filter(p -> p.getRuta().getIdRuta().equals(ruta.getIdRuta()))
                                        .findFirst()
                                        .orElse(null);
                                
                                return RutaMenuDTO.builder()
                                        .id(ruta.getIdRuta())
                                        .ruta(ruta.getRuta())
                                        .descripcion(ruta.getDescripcion())
                                        .puedeLeer(permiso != null && permiso.isPuedeLeer())
                                        .puedeEscribir(permiso != null && permiso.isPuedeEscribir())
                                        .puedeActualizar(permiso != null && permiso.isPuedeActualizar())
                                        .puedeEliminar(permiso != null && permiso.isPuedeEliminar())
                                        .build();
                            })
                            .collect(Collectors.toList());
                    
                    return ModuloMenuDTO.builder()
                            .id(modulo.getIdModulo())
                            .nombre(modulo.getNombre())
                            .descripcion(modulo.getDescripcion())
                            .rutas(rutasDTO)
                            .build();
                })
                .collect(Collectors.toList());
        
        return new MenuResponseDTO(modulosDTO);
    }
}

package com.claude.springboot.app.security.service;

import com.claude.springboot.app.security.enums.TipoPermiso;
import com.claude.springboot.app.security.repositories.PermisoRolRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermisoService {
    
    private final PermisoRolRepository permisoRolRepository;
    
    public boolean tienePermiso(Long idRol, String ruta, TipoPermiso tipoPermiso) {
        log.info("Verificando permiso - Rol: {}, Ruta: {}, Tipo: {}", idRol, ruta, tipoPermiso);
        
        boolean tienePermiso = false;
        try { 
            switch (tipoPermiso) {
                case LECTURA:
                    tienePermiso = permisoRolRepository.tienePermisoLectura(idRol, ruta);
                    break;
                case ESCRITURA:
                    tienePermiso = permisoRolRepository.tienePermisoEscritura(idRol, ruta);
                    break;
                case ACTUALIZAR:
                    tienePermiso = permisoRolRepository.tienePermisoActualizacion(idRol, ruta);
                    break;
                case ELIMINAR:
                    tienePermiso = permisoRolRepository.tienePermisoEliminacion(idRol, ruta);
                    break;
            }
            log.info("Resultado de verificación de permiso: {}", tienePermiso);
        } catch (Exception e) {
            log.error("Error verificando permiso", e);
        }
        return tienePermiso;
    }
}


package com.claude.springboot.app.security.evaluator;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.claude.springboot.app.security.enums.TipoPermiso;
import com.claude.springboot.app.security.repositories.PermisoRolRepository;

@Slf4j
@Component("permisoEvaluator")
@RequiredArgsConstructor
public class PermisoEvaluator {
    
    private final PermisoRolRepository permisoRolRepository;

    
    
    public boolean tienePermiso(String ruta, TipoPermiso tipoPermiso) {
        try {
            // Primero verificar si la ruta es pública
            boolean esPublica = permisoRolRepository.esRutaPublica(ruta);
            if (esPublica) {
                log.info("Ruta {} es PÚBLICA - Acceso permitido", ruta);
                return true;
            }
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                log.warn("No hay autenticación o usuario no está autenticado para ruta privada: {}", ruta);
                return false;
            }
            
            String rolNombre = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse(null);
                
            if (rolNombre == null) {
                log.warn("No se encontró rol para el usuario");
                return false;
            }
            

            
            log.info("Verificando permiso - Rol: {}, Ruta: {}, TipoPermiso: {}", 
                    rolNombre, ruta, tipoPermiso);
            
            boolean resultado = switch (tipoPermiso) {
                case LECTURA -> {
                    boolean permiso = permisoRolRepository.tienePermisoLecturaPorNombre(rolNombre, ruta);
                    log.debug("Consulta de permiso de lectura - Rol: {}, Ruta: {}, Resultado: {}", rolNombre, ruta, permiso);
                    yield permiso;
                }
                case ESCRITURA -> permisoRolRepository.tienePermisoEscrituraPorNombre(rolNombre, ruta);
                case ACTUALIZAR -> permisoRolRepository.tienePermisoActualizarPorNombre(rolNombre, ruta);
                case ELIMINAR -> permisoRolRepository.tienePermisoEliminarPorNombre(rolNombre, ruta);
                default -> {
                    log.warn("Tipo de permiso no reconocido: {}", tipoPermiso);
                    yield false;
                }
            };
            
            log.info("Resultado de verificación de permiso: {}", resultado);
            
            // Debug adicional si el resultado es false
            if (!resultado && tipoPermiso == TipoPermiso.LECTURA) {
                log.debug("PERMISO DENEGADO - Verificando datos en BD para debug...");
                log.debug("Rol buscado: '{}', Ruta buscada: '{}'", rolNombre, ruta);
            }
            return resultado;
            
        } catch (Exception e) {
            log.error("Error verificando permisos", e);
            return false;
        }
    }
}

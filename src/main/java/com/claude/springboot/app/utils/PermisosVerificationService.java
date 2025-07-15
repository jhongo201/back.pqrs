package com.claude.springboot.app.utils;

import com.claude.springboot.app.security.entities.Ruta;
import com.claude.springboot.app.security.entities.Rol;
import com.claude.springboot.app.security.repositories.RutaRepository;
import com.claude.springboot.app.security.repositories.PermisoRolRepository;
import com.claude.springboot.app.security.repositories.RolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermisosVerificationService implements CommandLineRunner {

    private final RutaRepository rutaRepository;
    private final PermisoRolRepository permisoRolRepository;
    private final RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("=== VERIFICACIÓN DE PERMISOS ===");
            
            // Verificar rutas existentes
            List<Ruta> rutas = rutaRepository.findAll();
            log.info("Rutas encontradas en la base de datos: {}", rutas.size());
            for (Ruta ruta : rutas) {
                log.info("- ID: {}, Ruta: {}, Descripción: {}", 
                    ruta.getIdRuta(), ruta.getRuta(), ruta.getDescripcion());
            }
            
            // Verificar si existe la ruta /api/permisorols
            Optional<Ruta> rutaPermisoRols = rutaRepository.findByRuta("/api/permisorols");
            if (rutaPermisoRols.isEmpty()) {
                log.warn("No se encontró la ruta /api/permisorols en la base de datos");
                
                // Verificar si existe /api/permisos-rol
                Optional<Ruta> rutaPermisoRolAntigua = rutaRepository.findByRuta("/api/permisos-rol");
                if (rutaPermisoRolAntigua.isPresent()) {
                    log.info("Se encontró la ruta antigua /api/permisos-rol, actualizándola...");
                    Ruta ruta = rutaPermisoRolAntigua.get();
                    ruta.setRuta("/api/permisorols");
                    rutaRepository.save(ruta);
                    log.info("Ruta actualizada exitosamente");
                } else {
                    log.warn("Tampoco se encontró la ruta /api/permisos-rol");
                    // No crear ruta automáticamente para evitar errores
                }
            } else {
                log.info("La ruta /api/permisorols ya existe en la base de datos");
            }
            
            // Verificar permisos del rol USUARIO
            verificarPermisosUsuario();
            
            log.info("=== FIN VERIFICACIÓN DE PERMISOS ===");
        } catch (Exception e) {
            log.error("Error durante la verificación de permisos: {}", e.getMessage(), e);
        }
    }
    
    private void verificarPermisosUsuario() {
        log.info("Verificando permisos del rol USUARIO para la ruta /api/permisorols");
        
        try {
            // Verificar si la ruta existe
            Optional<Ruta> rutaOpt = rutaRepository.findByRuta("/api/permisorols");
            if (rutaOpt.isEmpty()) {
                log.error("La ruta /api/permisorols NO EXISTE en la base de datos");
                log.info("SOLUCIÓN: Ejecutar el script fix_permisos.sql para crear la ruta y el permiso");
                return;
            } else {
                log.info("La ruta /api/permisorols existe en la base de datos - OK");
            }
            
            // Verificar si el rol USUARIO existe
            Optional<Rol> rolOpt = rolRepository.findByNombre("USUARIO");
            if (rolOpt.isEmpty()) {
                log.error("El rol USUARIO NO EXISTE en la base de datos");
                return;
            } else {
                log.info("El rol USUARIO existe en la base de datos - OK");
            }
            
            // Verificar si tiene permiso de lectura
            boolean tienePermiso = permisoRolRepository.tienePermisoLecturaPorNombre("USUARIO", "/api/permisorols");
            
            if (!tienePermiso) {
                log.error("PROBLEMA: El rol USUARIO NO tiene permiso de lectura en /api/permisorols");
                log.info("SOLUCIÓN: Ejecutar el script fix_permisos.sql para crear el permiso de lectura");
            } else {
                log.info("El rol USUARIO tiene permiso de lectura en /api/permisorols - OK");
            }
            
        } catch (Exception e) {
            log.error("Error al verificar permisos: {}", e.getMessage(), e);
        }
    }
}

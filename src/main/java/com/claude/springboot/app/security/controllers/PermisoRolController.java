package com.claude.springboot.app.security.controllers;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claude.springboot.app.security.annotations.PermitirActualizar;
import com.claude.springboot.app.security.annotations.PermitirEliminar;
import com.claude.springboot.app.security.annotations.PermitirEscritura;
import com.claude.springboot.app.security.annotations.PermitirLectura;
import com.claude.springboot.app.security.dto.AsignacionPermisosDTO;
import com.claude.springboot.app.security.dto.PermisoRolDTO;
import com.claude.springboot.app.security.service.PermisoRolService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/permisos-rol")
@RequiredArgsConstructor
@Slf4j
public class PermisoRolController {

    private final PermisoRolService permisoRolService;

    @GetMapping("/rol/{idRol}")
    @PermitirLectura
    public ResponseEntity<?> listarPermisosPorRol(@PathVariable Long idRol) {
        try {
            List<PermisoRolDTO> permisos = permisoRolService.listarPermisosPorRol(idRol);
            return ResponseEntity.ok(permisos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/asignar")
    @PermitirEscritura
    public ResponseEntity<?> asignarPermisos(@Valid @RequestBody AsignacionPermisosDTO asignacionDTO) {
        try {
            permisoRolService.asignarPermisosARol(asignacionDTO);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Permisos asignados correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{idPermiso}")
    @PermitirActualizar
    public ResponseEntity<?> actualizarPermiso(@PathVariable Long idPermiso, 
                                              @Valid @RequestBody PermisoRolDTO permisoDTO) {
        try {
            PermisoRolDTO permisoActualizado = permisoRolService.actualizarPermiso(idPermiso, permisoDTO);
            return ResponseEntity.ok(permisoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{idPermiso}")
    @PermitirEliminar
    public ResponseEntity<?> eliminarPermiso(@PathVariable Long idPermiso) {
        try {
            permisoRolService.eliminarPermiso(idPermiso);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Permiso eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}

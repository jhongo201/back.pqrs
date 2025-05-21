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
import com.claude.springboot.app.security.annotations.PublicEndpoint;
import com.claude.springboot.app.security.dto.RolDTO;
import com.claude.springboot.app.security.service.RolService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
public class RolController {

    private final RolService rolService;

    @GetMapping
    //@PermitirLectura
    @PublicEndpoint
    public ResponseEntity<?> listarRoles() {
        try {
            List<RolDTO> roles = rolService.listarTodos();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            log.error("Error al listar roles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PermitirLectura
    public ResponseEntity<?> obtenerRol(@PathVariable Long id) {
        try {
            RolDTO rol = rolService.obtenerPorId(id);
            return ResponseEntity.ok(rol);
        } catch (Exception e) {
            log.error("Error al obtener rol", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping
    @PermitirEscritura
    public ResponseEntity<?> crearRol(@Valid @RequestBody RolDTO rolDTO) {
        try {
            RolDTO nuevoRol = rolService.crear(rolDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
        } catch (Exception e) {
            log.error("Error al crear rol", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PermitirActualizar
    public ResponseEntity<?> actualizarRol(@PathVariable Long id, 
                                          @Valid @RequestBody RolDTO rolDTO) {
        try {
            RolDTO rolActualizado = rolService.actualizar(id, rolDTO);
            return ResponseEntity.ok(rolActualizado);
        } catch (Exception e) {
            log.error("Error al actualizar rol", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PermitirEliminar
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {
        try {
            rolService.eliminar(id);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Rol eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar rol", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}

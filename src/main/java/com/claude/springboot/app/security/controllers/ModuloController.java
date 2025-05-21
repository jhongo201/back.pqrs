package com.claude.springboot.app.security.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claude.springboot.app.security.annotations.PermitirActualizar;
import com.claude.springboot.app.security.annotations.PermitirEliminar;
import com.claude.springboot.app.security.annotations.PermitirEscritura;
import com.claude.springboot.app.security.annotations.PermitirLectura;
import com.claude.springboot.app.security.dto.ModuloDTO;
import com.claude.springboot.app.security.service.ModuloService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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


@RestController
@RequestMapping("/api/modulos")
@RequiredArgsConstructor
@Slf4j
public class ModuloController {

    private final ModuloService moduloService;

    @GetMapping
    @PermitirLectura
    public ResponseEntity<?> listarModulos() {
        try {
            List<ModuloDTO> modulos = moduloService.listarTodos();
            return ResponseEntity.ok(modulos);
        } catch (Exception e) {
            log.error("Error al listar módulos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PermitirLectura
    public ResponseEntity<?> obtenerModulo(@PathVariable Long id) {
        try {
            ModuloDTO modulo = moduloService.obtenerPorId(id);
            return ResponseEntity.ok(modulo);
        } catch (Exception e) {
            log.error("Error al obtener módulo", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping
    @PermitirEscritura
    public ResponseEntity<?> crearModulo(@Valid @RequestBody ModuloDTO moduloDTO) {
        try {
            ModuloDTO nuevoModulo = moduloService.crear(moduloDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoModulo);
        } catch (Exception e) {
            log.error("Error al crear módulo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PermitirActualizar
    public ResponseEntity<?> actualizarModulo(@PathVariable Long id, 
                                            @Valid @RequestBody ModuloDTO moduloDTO) {
        try {
            ModuloDTO moduloActualizado = moduloService.actualizar(id, moduloDTO);
            return ResponseEntity.ok(moduloActualizado);
        } catch (Exception e) {
            log.error("Error al actualizar módulo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PermitirEliminar
    public ResponseEntity<?> eliminarModulo(@PathVariable Long id) {
        try {
            moduloService.eliminar(id);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Módulo eliminado correctamente"));
        } catch (Exception e) {
            log.error("Error al eliminar módulo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}

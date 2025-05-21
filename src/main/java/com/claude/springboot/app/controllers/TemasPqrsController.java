package com.claude.springboot.app.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claude.springboot.app.dto.ActualizarResponsableDTO;
import com.claude.springboot.app.dto.TemasPqrsDTO;
import com.claude.springboot.app.dto.VincularResponsableDTO;
import com.claude.springboot.app.security.annotations.PermitirActualizar;
import com.claude.springboot.app.security.annotations.PermitirEliminar;
import com.claude.springboot.app.security.annotations.PermitirEscritura;
import com.claude.springboot.app.security.annotations.PermitirLectura;
import com.claude.springboot.app.security.annotations.PublicEndpoint;
import com.claude.springboot.app.services.TemasPqrsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/temas-pqrs")
@RequiredArgsConstructor
@Slf4j
public class TemasPqrsController {

    private final TemasPqrsService temasPqrsService;

    @PostMapping
    @PermitirEscritura
    public ResponseEntity<?> crear(@Valid @RequestBody TemasPqrsDTO dto) {
        try {
            return ResponseEntity.ok(temasPqrsService.crear(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PermitirActualizar
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody TemasPqrsDTO dto) {
        try {
            return ResponseEntity.ok(temasPqrsService.actualizar(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PermitirEliminar
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            temasPqrsService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Tema eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PermitirLectura
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(temasPqrsService.obtenerPorId(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
   // @PermitirLectura
    @PublicEndpoint
    public ResponseEntity<?> listarTodos() {
        try {
            return ResponseEntity.ok(temasPqrsService.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/responsables")
    @PermitirEscritura
    public ResponseEntity<?> vincularResponsable(
            @PathVariable Long id,
            @Valid @RequestBody VincularResponsableDTO dto) {
        try {
            return ResponseEntity.ok(temasPqrsService.vincularResponsable(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{idTema}/responsables/{idUsuario}")
    @PermitirEliminar
    public ResponseEntity<?> desvincularResponsable(
            @PathVariable Long idTema,
            @PathVariable Long idUsuario) {
        try {
            return ResponseEntity.ok(temasPqrsService.desvincularResponsable(idTema, idUsuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/responsables")
    @PermitirActualizar
    public ResponseEntity<?> actualizarResponsable(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarResponsableDTO dto) {
        try {
            return ResponseEntity.ok(temasPqrsService.actualizarResponsable(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}

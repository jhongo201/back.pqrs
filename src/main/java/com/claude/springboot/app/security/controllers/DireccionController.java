package com.claude.springboot.app.security.controllers;

import java.util.Map;

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
import com.claude.springboot.app.security.dto.DireccionDTO;
import com.claude.springboot.app.security.service.DireccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/direcciones")
@RequiredArgsConstructor
@Slf4j
public class DireccionController {

   private final DireccionService direccionService;

   @GetMapping
   @PermitirLectura
   public ResponseEntity<?> listarDirecciones() {
       return ResponseEntity.ok(direccionService.listarTodos());
   }

   @GetMapping("/{id}")
   @PermitirLectura
   public ResponseEntity<?> obtenerDireccion(@PathVariable Long id) {
       return ResponseEntity.ok(direccionService.obtenerPorId(id));
   }

   @PostMapping
   @PermitirEscritura
   public ResponseEntity<?> crearDireccion(@Valid @RequestBody DireccionDTO direccionDTO) {
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(direccionService.crear(direccionDTO));
   }

   @PutMapping("/{id}")
   @PermitirActualizar
   public ResponseEntity<?> actualizarDireccion(@PathVariable Long id, 
                                               @Valid @RequestBody DireccionDTO direccionDTO) {
       return ResponseEntity.ok(direccionService.actualizar(id, direccionDTO));
   }

   @DeleteMapping("/{id}")
   @PermitirEliminar
   public ResponseEntity<?> eliminarDireccion(@PathVariable Long id) {
       direccionService.eliminar(id);
       //return ResponseEntity.ok().build();
       return ResponseEntity.ok(Map.of("mensaje", "Dirección eliminada correctamente"));
   }
}

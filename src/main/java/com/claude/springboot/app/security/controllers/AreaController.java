package com.claude.springboot.app.security.controllers;

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
import com.claude.springboot.app.security.dto.AreaDTO;
import com.claude.springboot.app.security.service.AreaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
@Slf4j
public class AreaController {

   private final AreaService areaService;

   @GetMapping
   @PermitirLectura
   public ResponseEntity<?> listarAreas() {
       return ResponseEntity.ok(areaService.listarTodos());
   }

   @GetMapping("/{id}")
   @PermitirLectura
   public ResponseEntity<?> obtenerArea(@PathVariable Long id) {
       return ResponseEntity.ok(areaService.obtenerPorId(id));
   }

   @PostMapping
   @PermitirEscritura
   public ResponseEntity<?> crearArea(@Valid @RequestBody AreaDTO areaDTO) {
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(areaService.crear(areaDTO));
   }

   @PutMapping("/{id}")
   @PermitirActualizar
   public ResponseEntity<?> actualizarArea(@PathVariable Long id, 
                                         @Valid @RequestBody AreaDTO areaDTO) {
       return ResponseEntity.ok(areaService.actualizar(id, areaDTO));
   }

   @DeleteMapping("/{id}")
   @PermitirEliminar
   public ResponseEntity<?> eliminarArea(@PathVariable Long id) {
       areaService.eliminar(id);
       return ResponseEntity.ok().build();
   }
}

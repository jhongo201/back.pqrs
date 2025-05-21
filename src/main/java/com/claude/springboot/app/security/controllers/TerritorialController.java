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
import com.claude.springboot.app.security.dto.TerritorialDTO;
import com.claude.springboot.app.security.service.TerritorialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/territoriales")
@RequiredArgsConstructor
@Slf4j
public class TerritorialController {

    private final TerritorialService territorialService;

    @GetMapping
    @PermitirLectura
    public ResponseEntity<?> listarTerritoriales() {
        return ResponseEntity.ok(territorialService.listarTodos());
    }

    @GetMapping("/{id}")
    @PermitirLectura
    public ResponseEntity<?> obtenerTerritorial(@PathVariable Long id) {
        return ResponseEntity.ok(territorialService.obtenerPorId(id));
    }

    @PostMapping
    @PermitirEscritura
    public ResponseEntity<?> crearTerritorial(@Valid @RequestBody TerritorialDTO territorialDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(territorialService.crear(territorialDTO));
    }

    @PutMapping("/{id}")
    @PermitirActualizar
    public ResponseEntity<?> actualizarTerritorial(@PathVariable Long id, 
                                                  @Valid @RequestBody TerritorialDTO territorialDTO) {
        return ResponseEntity.ok(territorialService.actualizar(id, territorialDTO));
    }

    @DeleteMapping("/{id}")
    @PermitirEliminar
    public ResponseEntity<?> eliminarTerritorial(@PathVariable Long id) {
        territorialService.eliminar(id);
        //return ResponseEntity.ok().build();
        return ResponseEntity.ok(Map.of("mensaje", "Territorial eliminada correctamente"));
    }
}

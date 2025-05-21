package com.claude.springboot.app.security.controllers;

import java.time.LocalDateTime;
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
import com.claude.springboot.app.security.dto.ActualizarRutaDTO;
import com.claude.springboot.app.security.dto.RegistroRutaDTO;
import com.claude.springboot.app.security.dto.RutaResponseDTO;
import com.claude.springboot.app.security.entities.ErrorResponse;
import com.claude.springboot.app.security.service.RutaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaController {
    
    private final RutaService rutaService;
    
    @PostMapping
    @PermitirEscritura
    public ResponseEntity<?> registrarRuta(@Valid @RequestBody RegistroRutaDTO registroDTO) {
        try {
            RutaResponseDTO ruta = rutaService.registrarRuta(registroDTO);
            return ResponseEntity.ok(ruta);
        } catch (IllegalArgumentException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage(), LocalDateTime.now());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                "Error al procesar la solicitud: " + e.getMessage(), 
                LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
    //@PermitirLectura
    @PublicEndpoint
    public ResponseEntity<List<RutaResponseDTO>> listarRutas() {
        return ResponseEntity.ok(rutaService.listarRutas());
    }
    
    @GetMapping("/{id}")
    @PermitirLectura
    public ResponseEntity<RutaResponseDTO> obtenerRuta(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.obtenerRuta(id));
    }
    
    @PutMapping("/{id}")
    @PermitirActualizar
    public ResponseEntity<RutaResponseDTO> actualizarRuta(
            @PathVariable Long id, 
            @Valid @RequestBody ActualizarRutaDTO actualizarDTO) {
        if (!id.equals(actualizarDTO.getIdRuta())) {
            throw new IllegalArgumentException("El ID de la ruta no coincide con el ID en la URL");
        }
        return ResponseEntity.ok(rutaService.actualizarRuta(actualizarDTO));
    }
    
    @DeleteMapping("/{id}")
    @PermitirEliminar
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }
}

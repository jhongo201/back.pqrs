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
import com.claude.springboot.app.security.dto.EmpresaDTO;
import com.claude.springboot.app.security.service.EmpresaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@Slf4j
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    @PermitirLectura
    public ResponseEntity<?> listarEmpresas() {
        return ResponseEntity.ok(empresaService.listarTodos());
    }

    @GetMapping("/{id}")
    @PermitirLectura
    public ResponseEntity<?> obtenerEmpresa(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtenerPorId(id));
    }

    @PostMapping
    @PermitirEscritura
    public ResponseEntity<?> crearEmpresa(@Valid @RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(empresaService.crear(empresaDTO));
    }

    @PutMapping("/{id}")
    @PermitirActualizar
    public ResponseEntity<?> actualizarEmpresa(@PathVariable Long id, 
                                              @Valid @RequestBody EmpresaDTO empresaDTO) {
        return ResponseEntity.ok(empresaService.actualizar(id, empresaDTO));
    }

    @DeleteMapping("/{id}")
    @PermitirEliminar
    public ResponseEntity<?> eliminarEmpresa(@PathVariable Long id) {
        empresaService.eliminar(id);
       //return ResponseEntity.ok().build();
       return ResponseEntity.ok(Map.of("mensaje", "Empresa eliminada correctamente"));
    }
}

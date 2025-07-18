package com.claude.springboot.app.controllers;

import com.claude.springboot.app.dto.DepartamentoDTO;
import com.claude.springboot.app.security.annotations.PermitirLectura;
import com.claude.springboot.app.security.annotations.PermitirEscritura;
import com.claude.springboot.app.security.annotations.PermitirActualizar;
import com.claude.springboot.app.services.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*")
public class DepartamentoController {
    
    @Autowired
    private DepartamentoService departamentoService;
    
    /**
     * Obtener todos los departamentos
     */
    @GetMapping
    @PermitirLectura
    public ResponseEntity<List<DepartamentoDTO>> obtenerTodosLosDepartamentos() {
        List<DepartamentoDTO> departamentos = departamentoService.obtenerTodosLosDepartamentos();
        return ResponseEntity.ok(departamentos);
    }
    
    /**
     * Obtener departamento por código DANE
     */
    @GetMapping("/{codigoDane}")
    @PermitirLectura
    public ResponseEntity<DepartamentoDTO> obtenerDepartamentoPorCodigo(@PathVariable String codigoDane) {
        Optional<DepartamentoDTO> departamento = departamentoService.obtenerDepartamentoPorCodigo(codigoDane);
        return departamento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Buscar departamentos por nombre
     */
    @GetMapping("/buscar")
    @PermitirLectura
    public ResponseEntity<List<DepartamentoDTO>> buscarDepartamentosPorNombre(@RequestParam String nombre) {
        List<DepartamentoDTO> departamentos = departamentoService.buscarDepartamentosPorNombre(nombre);
        return ResponseEntity.ok(departamentos);
    }
    
    /**
     * Obtener departamentos por región
     */
    @GetMapping("/region/{region}")
    @PermitirLectura
    public ResponseEntity<List<DepartamentoDTO>> obtenerDepartamentosPorRegion(@PathVariable String region) {
        List<DepartamentoDTO> departamentos = departamentoService.obtenerDepartamentosPorRegion(region);
        return ResponseEntity.ok(departamentos);
    }
    
    /**
     * Buscar departamentos por capital
     */
    @GetMapping("/capital")
    @PermitirLectura
    public ResponseEntity<List<DepartamentoDTO>> buscarDepartamentosPorCapital(@RequestParam String capital) {
        List<DepartamentoDTO> departamentos = departamentoService.buscarDepartamentosPorCapital(capital);
        return ResponseEntity.ok(departamentos);
    }
    
    /**
     * Obtener todas las regiones
     */
    @GetMapping("/regiones")
    @PermitirLectura
    public ResponseEntity<List<String>> obtenerRegiones() {
        List<String> regiones = departamentoService.obtenerRegiones();
        return ResponseEntity.ok(regiones);
    }
    
    /**
     * Obtener estadísticas de departamentos
     */
    @GetMapping("/estadisticas")
    @PermitirLectura
    public ResponseEntity<List<DepartamentoDTO>> obtenerEstadisticasDepartamentos() {
        List<DepartamentoDTO> estadisticas = departamentoService.obtenerEstadisticasDepartamentos();
        return ResponseEntity.ok(estadisticas);
    }
    
    /**
     * Obtener departamentos con municipios
     */
    @GetMapping("/con-municipios")
    @PermitirLectura
    public ResponseEntity<List<DepartamentoDTO>> obtenerDepartamentosConMunicipios() {
        List<DepartamentoDTO> departamentos = departamentoService.obtenerDepartamentosConMunicipios();
        return ResponseEntity.ok(departamentos);
    }
    
    /**
     * Crear nuevo departamento
     */
    @PostMapping
    @PermitirEscritura
    public ResponseEntity<DepartamentoDTO> crearDepartamento(@Valid @RequestBody DepartamentoDTO departamentoDTO) {
        try {
            DepartamentoDTO nuevoDepartamento = departamentoService.crearDepartamento(departamentoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDepartamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Actualizar departamento
     */
    @PutMapping("/{codigoDane}")
    @PermitirActualizar
    public ResponseEntity<DepartamentoDTO> actualizarDepartamento(
            @PathVariable String codigoDane,
            @Valid @RequestBody DepartamentoDTO departamentoDTO) {
        try {
            DepartamentoDTO departamentoActualizado = departamentoService.actualizarDepartamento(codigoDane, departamentoDTO);
            return ResponseEntity.ok(departamentoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Desactivar departamento
     */
    @DeleteMapping("/{codigoDane}")
    @PermitirActualizar
    public ResponseEntity<Void> desactivarDepartamento(@PathVariable String codigoDane) {
        try {
            departamentoService.desactivarDepartamento(codigoDane);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Activar departamento
     */
    @PatchMapping("/{codigoDane}/activar")
    @PermitirActualizar
    public ResponseEntity<Void> activarDepartamento(@PathVariable String codigoDane) {
        try {
            departamentoService.activarDepartamento(codigoDane);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Verificar si existe un departamento
     */
    @GetMapping("/{codigoDane}/existe")
    @PermitirLectura
    public ResponseEntity<Boolean> existeDepartamento(@PathVariable String codigoDane) {
        boolean existe = departamentoService.existeDepartamento(codigoDane);
        return ResponseEntity.ok(existe);
    }
}

package com.claude.springboot.app.controllers;

import com.claude.springboot.app.dto.MunicipioDTO;
import com.claude.springboot.app.security.annotations.PermitirLectura;
import com.claude.springboot.app.security.annotations.PermitirEscritura;
import com.claude.springboot.app.security.annotations.PermitirActualizar;
import com.claude.springboot.app.services.MunicipioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/municipios")
@CrossOrigin(origins = "*")
public class MunicipioController {
    
    @Autowired
    private MunicipioService municipioService;
    
    /**
     * Obtener todos los municipios
     */
    @GetMapping
    @PermitirLectura
    public ResponseEntity<List<MunicipioDTO>> obtenerTodosLosMunicipios() {
        List<MunicipioDTO> municipios = municipioService.obtenerTodosLosMunicipios();
        return ResponseEntity.ok(municipios);
    }
    
    /**
     * Obtener municipio por código DANE
     */
    @GetMapping("/{codigoDane}")
    @PermitirLectura
    public ResponseEntity<MunicipioDTO> obtenerMunicipioPorCodigo(@PathVariable String codigoDane) {
        Optional<MunicipioDTO> municipio = municipioService.obtenerMunicipioPorCodigo(codigoDane);
        return municipio.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtener municipios por departamento
     */
    @GetMapping("/departamento/{codigoDepartamento}")
    @PermitirLectura
    public ResponseEntity<List<MunicipioDTO>> obtenerMunicipiosPorDepartamento(@PathVariable String codigoDepartamento) {
        List<MunicipioDTO> municipios = municipioService.obtenerMunicipiosPorDepartamento(codigoDepartamento);
        return ResponseEntity.ok(municipios);
    }
    
    /**
     * Buscar municipios por nombre
     */
    @GetMapping("/buscar")
    @PermitirLectura
    public ResponseEntity<List<MunicipioDTO>> buscarMunicipiosPorNombre(@RequestParam String nombre) {
        List<MunicipioDTO> municipios = municipioService.buscarMunicipiosPorNombre(nombre);
        return ResponseEntity.ok(municipios);
    }
    
    /**
     * Obtener municipios por categoría
     */
    @GetMapping("/categoria/{categoria}")
    @PermitirLectura
    public ResponseEntity<List<MunicipioDTO>> obtenerMunicipiosPorCategoria(@PathVariable String categoria) {
        List<MunicipioDTO> municipios = municipioService.obtenerMunicipiosPorCategoria(categoria);
        return ResponseEntity.ok(municipios);
    }
    
    /**
     * Obtener municipios por población mínima
     */
    @GetMapping("/poblacion-minima/{poblacion}")
    @PermitirLectura
    public ResponseEntity<List<MunicipioDTO>> obtenerMunicipiosPorPoblacionMinima(@PathVariable Integer poblacion) {
        List<MunicipioDTO> municipios = municipioService.obtenerMunicipiosPorPoblacionMinima(poblacion);
        return ResponseEntity.ok(municipios);
    }
    
    /**
     * Buscar municipios por nombre y departamento
     */
    @GetMapping("/buscar-avanzado")
    @PermitirLectura
    public ResponseEntity<List<MunicipioDTO>> buscarMunicipiosPorNombreYDepartamento(
            @RequestParam String nombreMunicipio,
            @RequestParam String nombreDepartamento) {
        List<MunicipioDTO> municipios = municipioService.buscarMunicipiosPorNombreYDepartamento(nombreMunicipio, nombreDepartamento);
        return ResponseEntity.ok(municipios);
    }
    
    /**
     * Obtener municipios con información completa del departamento
     */
    @GetMapping("/con-departamento")
    @PermitirLectura
    public ResponseEntity<List<MunicipioDTO>> obtenerMunicipiosConDepartamento() {
        List<MunicipioDTO> municipios = municipioService.obtenerMunicipiosConDepartamento();
        return ResponseEntity.ok(municipios);
    }
    
    /**
     * Obtener todas las categorías
     */
    @GetMapping("/categorias")
    @PermitirLectura
    public ResponseEntity<List<String>> obtenerCategorias() {
        List<String> categorias = municipioService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }
    
    /**
     * Obtener municipios por región
     */
    @GetMapping("/region/{region}")
    @PermitirLectura
    public ResponseEntity<List<MunicipioDTO>> obtenerMunicipiosPorRegion(@PathVariable String region) {
        List<MunicipioDTO> municipios = municipioService.obtenerMunicipiosPorRegion(region);
        return ResponseEntity.ok(municipios);
    }
    
    /**
     * Obtener municipios más poblados
     */
    @GetMapping("/mas-poblados")
    @PermitirLectura
    public ResponseEntity<List<MunicipioDTO>> obtenerMunicipiosMasPoblados(@RequestParam(defaultValue = "10") int limite) {
        List<MunicipioDTO> municipios = municipioService.obtenerMunicipiosMasPoblados(limite);
        return ResponseEntity.ok(municipios);
    }
    
    /**
     * Crear nuevo municipio
     */
    @PostMapping
    @PermitirEscritura
    public ResponseEntity<MunicipioDTO> crearMunicipio(@Valid @RequestBody MunicipioDTO municipioDTO) {
        try {
            MunicipioDTO nuevoMunicipio = municipioService.crearMunicipio(municipioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMunicipio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Actualizar municipio
     */
    @PutMapping("/{codigoDane}")
    @PermitirActualizar
    public ResponseEntity<MunicipioDTO> actualizarMunicipio(
            @PathVariable String codigoDane,
            @Valid @RequestBody MunicipioDTO municipioDTO) {
        try {
            MunicipioDTO municipioActualizado = municipioService.actualizarMunicipio(codigoDane, municipioDTO);
            return ResponseEntity.ok(municipioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Desactivar municipio
     */
    @DeleteMapping("/{codigoDane}")
    @PermitirActualizar
    public ResponseEntity<Void> desactivarMunicipio(@PathVariable String codigoDane) {
        try {
            municipioService.desactivarMunicipio(codigoDane);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Activar municipio
     */
    @PatchMapping("/{codigoDane}/activar")
    @PermitirActualizar
    public ResponseEntity<Void> activarMunicipio(@PathVariable String codigoDane) {
        try {
            municipioService.activarMunicipio(codigoDane);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Verificar si existe un municipio
     */
    @GetMapping("/{codigoDane}/existe")
    @PermitirLectura
    public ResponseEntity<Boolean> existeMunicipio(@PathVariable String codigoDane) {
        boolean existe = municipioService.existeMunicipio(codigoDane);
        return ResponseEntity.ok(existe);
    }
    
    /**
     * Validar código DANE del municipio
     */
    @GetMapping("/validar-codigo")
    @PermitirLectura
    public ResponseEntity<Boolean> validarCodigoDaneMunicipio(
            @RequestParam String codigoMunicipio,
            @RequestParam String codigoDepartamento) {
        boolean valido = municipioService.validarCodigoDaneMunicipio(codigoMunicipio, codigoDepartamento);
        return ResponseEntity.ok(valido);
    }
    
    /**
     * Obtener estadísticas por departamento
     */
    @GetMapping("/estadisticas/departamento")
    @PermitirLectura
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorDepartamento() {
        List<Object[]> estadisticas = municipioService.obtenerEstadisticasPorDepartamento();
        return ResponseEntity.ok(estadisticas);
    }
    
    /**
     * Obtener estadísticas por categoría
     */
    @GetMapping("/estadisticas/categoria")
    @PermitirLectura
    public ResponseEntity<List<Object[]>> obtenerEstadisticasPorCategoria() {
        List<Object[]> estadisticas = municipioService.obtenerEstadisticasPorCategoria();
        return ResponseEntity.ok(estadisticas);
    }
}

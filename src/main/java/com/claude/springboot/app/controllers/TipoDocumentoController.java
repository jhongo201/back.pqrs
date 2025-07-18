package com.claude.springboot.app.controllers;

import com.claude.springboot.app.dto.TipoDocumentoDTO;
import com.claude.springboot.app.security.annotations.PermitirLectura;
import com.claude.springboot.app.security.annotations.PermitirEscritura;
import com.claude.springboot.app.security.annotations.PermitirActualizar;
import com.claude.springboot.app.services.TipoDocumentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de tipos de documento
 * 
 * @author Sistema PQRS
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/tipodocumentos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumentoService;

    /**
     * Obtener todos los tipos de documento
     * 
     * @return Lista de tipos de documento
     */
    @GetMapping
    @PermitirLectura
    public ResponseEntity<List<TipoDocumentoDTO>> obtenerTodosLosTiposDocumento() {
        log.info("Solicitando todos los tipos de documento");
        List<TipoDocumentoDTO> tiposDocumento = tipoDocumentoService.obtenerTodosLosTiposDocumento();
        log.info("Se encontraron {} tipos de documento", tiposDocumento.size());
        return ResponseEntity.ok(tiposDocumento);
    }

    /**
     * Obtener todos los tipos de documento activos
     * 
     * @return Lista de tipos de documento activos
     */
    @GetMapping("/activos")
    @PermitirLectura
    public ResponseEntity<List<TipoDocumentoDTO>> obtenerTiposDocumentoActivos() {
        log.info("Solicitando tipos de documento activos");
        List<TipoDocumentoDTO> tiposDocumento = tipoDocumentoService.obtenerTiposDocumentoActivos();
        log.info("Se encontraron {} tipos de documento activos", tiposDocumento.size());
        return ResponseEntity.ok(tiposDocumento);
    }

    /**
     * Obtener tipo de documento por ID
     * 
     * @param id ID del tipo de documento
     * @return Tipo de documento encontrado
     */
    @GetMapping("/{id}")
    @PermitirLectura
    public ResponseEntity<TipoDocumentoDTO> obtenerTipoDocumentoPorId(@PathVariable Long id) {
        log.info("Solicitando tipo de documento con ID: {}", id);
        Optional<TipoDocumentoDTO> tipoDocumento = tipoDocumentoService.obtenerTipoDocumentoPorId(id);
        
        if (tipoDocumento.isPresent()) {
            log.info("Tipo de documento encontrado: {}", tipoDocumento.get().getCodigo());
            return ResponseEntity.ok(tipoDocumento.get());
        } else {
            log.warn("No se encontró tipo de documento con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener tipo de documento por código
     * 
     * @param codigo Código del tipo de documento
     * @return Tipo de documento encontrado
     */
    @GetMapping("/codigo/{codigo}")
    @PermitirLectura
    public ResponseEntity<TipoDocumentoDTO> obtenerTipoDocumentoPorCodigo(@PathVariable String codigo) {
        log.info("Solicitando tipo de documento con código: {}", codigo);
        Optional<TipoDocumentoDTO> tipoDocumento = tipoDocumentoService.obtenerTipoDocumentoPorCodigo(codigo);
        
        if (tipoDocumento.isPresent()) {
            log.info("Tipo de documento encontrado: {}", tipoDocumento.get().getNombre());
            return ResponseEntity.ok(tipoDocumento.get());
        } else {
            log.warn("No se encontró tipo de documento con código: {}", codigo);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Buscar tipos de documento por término
     * 
     * @param termino Término de búsqueda
     * @return Lista de tipos de documento que coinciden
     */
    @GetMapping("/buscar")
    @PermitirLectura
    public ResponseEntity<List<TipoDocumentoDTO>> buscarTiposDocumento(@RequestParam String termino) {
        log.info("Buscando tipos de documento con término: {}", termino);
        List<TipoDocumentoDTO> tiposDocumento = tipoDocumentoService.buscarTiposDocumento(termino);
        log.info("Se encontraron {} tipos de documento que coinciden", tiposDocumento.size());
        return ResponseEntity.ok(tiposDocumento);
    }

    /**
     * Buscar tipos de documento activos por término
     * 
     * @param termino Término de búsqueda
     * @return Lista de tipos de documento activos que coinciden
     */
    @GetMapping("/buscar/activos")
    @PermitirLectura
    public ResponseEntity<List<TipoDocumentoDTO>> buscarTiposDocumentoActivos(@RequestParam String termino) {
        log.info("Buscando tipos de documento activos con término: {}", termino);
        List<TipoDocumentoDTO> tiposDocumento = tipoDocumentoService.buscarTiposDocumentoActivos(termino);
        log.info("Se encontraron {} tipos de documento activos que coinciden", tiposDocumento.size());
        return ResponseEntity.ok(tiposDocumento);
    }

    /**
     * Crear un nuevo tipo de documento
     * 
     * @param tipoDocumentoDTO Datos del tipo de documento a crear
     * @return Tipo de documento creado
     */
    @PostMapping
    @PermitirEscritura
    public ResponseEntity<TipoDocumentoDTO> crearTipoDocumento(@Valid @RequestBody TipoDocumentoDTO tipoDocumentoDTO) {
        log.info("Creando nuevo tipo de documento: {}", tipoDocumentoDTO.getCodigo());
        
        try {
            TipoDocumentoDTO tipoDocumentoCreado = tipoDocumentoService.crearTipoDocumento(tipoDocumentoDTO);
            log.info("Tipo de documento creado exitosamente con ID: {}", tipoDocumentoCreado.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(tipoDocumentoCreado);
        } catch (IllegalArgumentException e) {
            log.error("Error al crear tipo de documento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualizar un tipo de documento existente
     * 
     * @param id               ID del tipo de documento a actualizar
     * @param tipoDocumentoDTO Nuevos datos del tipo de documento
     * @return Tipo de documento actualizado
     */
    @PutMapping("/{id}")
    @PermitirActualizar
    public ResponseEntity<TipoDocumentoDTO> actualizarTipoDocumento(
            @PathVariable Long id,
            @Valid @RequestBody TipoDocumentoDTO tipoDocumentoDTO) {
        log.info("Actualizando tipo de documento con ID: {}", id);
        
        try {
            Optional<TipoDocumentoDTO> tipoDocumentoActualizado = tipoDocumentoService.actualizarTipoDocumento(id, tipoDocumentoDTO);
            
            if (tipoDocumentoActualizado.isPresent()) {
                log.info("Tipo de documento actualizado exitosamente: {}", id);
                return ResponseEntity.ok(tipoDocumentoActualizado.get());
            } else {
                log.warn("No se encontró tipo de documento con ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            log.error("Error al actualizar tipo de documento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Activar un tipo de documento
     * 
     * @param id ID del tipo de documento a activar
     * @return Respuesta de éxito o error
     */
    @PatchMapping("/{id}/activar")
    @PermitirActualizar
    public ResponseEntity<Void> activarTipoDocumento(@PathVariable Long id) {
        log.info("Activando tipo de documento con ID: {}", id);
        
        boolean activado = tipoDocumentoService.activarTipoDocumento(id);
        
        if (activado) {
            log.info("Tipo de documento activado exitosamente: {}", id);
            return ResponseEntity.ok().build();
        } else {
            log.warn("No se encontró tipo de documento con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Desactivar un tipo de documento
     * 
     * @param id ID del tipo de documento a desactivar
     * @return Respuesta de éxito o error
     */
    @PatchMapping("/{id}/desactivar")
    @PermitirActualizar
    public ResponseEntity<Void> desactivarTipoDocumento(@PathVariable Long id) {
        log.info("Desactivando tipo de documento con ID: {}", id);
        
        boolean desactivado = tipoDocumentoService.desactivarTipoDocumento(id);
        
        if (desactivado) {
            log.info("Tipo de documento desactivado exitosamente: {}", id);
            return ResponseEntity.ok().build();
        } else {
            log.warn("No se encontró tipo de documento con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar un tipo de documento
     * 
     * @param id ID del tipo de documento a eliminar
     * @return Respuesta de éxito o error
     */
    @DeleteMapping("/{id}")
    @PermitirActualizar
    public ResponseEntity<Void> eliminarTipoDocumento(@PathVariable Long id) {
        log.info("Eliminando tipo de documento con ID: {}", id);
        
        boolean eliminado = tipoDocumentoService.eliminarTipoDocumento(id);
        
        if (eliminado) {
            log.info("Tipo de documento eliminado exitosamente: {}", id);
            return ResponseEntity.ok().build();
        } else {
            log.warn("No se encontró tipo de documento con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener estadísticas de tipos de documento
     * 
     * @return Estadísticas [total, activos, inactivos]
     */
    @GetMapping("/estadisticas")
    @PermitirLectura
    public ResponseEntity<java.util.Map<String, Long>> obtenerEstadisticas() {
        log.info("Solicitando estadísticas de tipos de documento");
        Long[] estadisticas = tipoDocumentoService.obtenerEstadisticas();
        
        java.util.Map<String, Long> resultado = new java.util.HashMap<>();
        resultado.put("total", estadisticas[0]);
        resultado.put("activos", estadisticas[1]);
        resultado.put("inactivos", estadisticas[2]);
        
        return ResponseEntity.ok(resultado);
    }

    /**
     * Verificar si existe un tipo de documento por código
     * 
     * @param codigo Código a verificar
     * @return true si existe, false en caso contrario
     */
    @GetMapping("/existe/codigo/{codigo}")
    @PermitirLectura
    public ResponseEntity<Boolean> existeTipoDocumentoPorCodigo(@PathVariable String codigo) {
        log.info("Verificando existencia de tipo de documento con código: {}", codigo);
        boolean existe = tipoDocumentoService.existeTipoDocumentoPorCodigo(codigo);
        return ResponseEntity.ok(existe);
    }

    /**
     * Verificar si existe un tipo de documento por nombre
     * 
     * @param nombre Nombre a verificar
     * @return true si existe, false en caso contrario
     */
    @GetMapping("/existe/nombre")
    @PermitirLectura
    public ResponseEntity<Boolean> existeTipoDocumentoPorNombre(@RequestParam String nombre) {
        log.info("Verificando existencia de tipo de documento con nombre: {}", nombre);
        boolean existe = tipoDocumentoService.existeTipoDocumentoPorNombre(nombre);
        return ResponseEntity.ok(existe);
    }
}

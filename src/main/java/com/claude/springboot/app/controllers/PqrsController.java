package com.claude.springboot.app.controllers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.claude.springboot.app.dto.AsignarPqrsDTO;
import com.claude.springboot.app.dto.CrearPqrsDTO;
import com.claude.springboot.app.dto.CrearPqrsUsuarioRegistradoDTO;
import com.claude.springboot.app.dto.CrearSeguimientoDTO;
import com.claude.springboot.app.dto.HistorialAsignacionDTO;
import com.claude.springboot.app.dto.PqrsResponseDTO;
import com.claude.springboot.app.dto.RespuestaSolicitanteDTO;
import com.claude.springboot.app.repositories.PqrsRepository;
import com.claude.springboot.app.security.annotations.PermitirActualizar;
import com.claude.springboot.app.security.annotations.PermitirEscritura;
import com.claude.springboot.app.security.annotations.PermitirLectura;
import com.claude.springboot.app.security.annotations.PublicEndpoint;
import com.claude.springboot.app.security.service.UsuarioService;
import com.claude.springboot.app.services.FileStorageService;
import com.claude.springboot.app.services.HistorialAsignacionService;
import com.claude.springboot.app.services.PqrsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/pqrs")
@RequiredArgsConstructor
@Slf4j
public class PqrsController {
    
    private final PqrsService pqrsService;
    private final FileStorageService fileStorageService;
    private final HistorialAsignacionService historialService;
    private final UsuarioService usuarioService;
    private final PqrsRepository pqrsRepository;

    // Método para usuarios externos
    @PostMapping("/publico")
    public ResponseEntity<?> crearPqrsPublico(@Valid @RequestBody CrearPqrsDTO dto) {
        try {
             // Verificar si hay un usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                // Si hay un usuario autenticado, devolver una respuesta que indique redirección
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Usuario autenticado detectado");
                response.put("mensaje", "Por favor utilice el endpoint para usuarios registrados");
                response.put("endpointCorrecto", "/api/pqrs");
                response.put("tipo", "REDIRECCION_USUARIO_REGISTRADO");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Si no hay usuario autenticado, proceder normalmente
            return ResponseEntity.ok(pqrsService.crearPqrsPublico(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Nuevo endpoint para usuarios registrados
    @PostMapping
    @PermitirEscritura
    public ResponseEntity<?> crearPqrsUsuarioRegistrado(
            @RequestParam("idTema") Long idTema,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("prioridad") String prioridad,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
        try {
            PqrsResponseDTO response = pqrsService.crearPqrsUsuarioRegistrado(
                idTema, titulo, descripcion, prioridad, archivo);
                System.out.println("esta entrando: " + archivo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/asignar")
    @PermitirActualizar
    public ResponseEntity<?> asignar(
            @PathVariable Long id,
            @Valid @RequestBody AsignarPqrsDTO dto) {
        try {
            return ResponseEntity.ok(pqrsService.asignar(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/seguimiento")
@PermitirEscritura
public ResponseEntity<?> agregarSeguimiento(
        @PathVariable Long id,
        @RequestParam("comentario") String comentario,
        @RequestParam("esRespuestaFinal") boolean esRespuestaFinal,
        @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
    try {
        return ResponseEntity.ok(pqrsService.agregarSeguimiento(id, comentario, esRespuestaFinal, archivo));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}

    @PutMapping("/{id}/estado/{nuevoEstado}")
    @PermitirActualizar
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long id,
            @PathVariable String nuevoEstado) {
        try {
            return ResponseEntity.ok(pqrsService.actualizarEstado(id, nuevoEstado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/prioridad/{nuevaPrioridad}")
    @PermitirActualizar
    public ResponseEntity<?> actualizarPrioridad(
            @PathVariable Long id,
            @PathVariable String nuevaPrioridad) {
        try {
            return ResponseEntity.ok(pqrsService.actualizarPrioridad(id, nuevaPrioridad));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PermitirLectura
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pqrsService.obtenerPorId(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @PermitirLectura
    public ResponseEntity<?> listarTodos() {
        try {
            log.info("Iniciando listado de PQRS"); // Agregar log para debug
            List<PqrsResponseDTO> pqrs = pqrsService.listarTodos();
            log.info("PQRS recuperadas exitosamente: {}", pqrs.size());
            return ResponseEntity.ok(pqrs);
        } catch (Exception e) {
            log.error("Error al listar PQRS: ", e); // Agregar log del error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al listar PQRS",
                        "mensaje", e.getMessage()
                    ));
        }
    }

    @GetMapping("/radicado/{numeroRadicado}")
    public ResponseEntity<?> consultarPorRadicado(@PathVariable String numeroRadicado) {
        try {
            return ResponseEntity.ok(pqrsService.consultarPorRadicado(numeroRadicado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mis-pqrs")
    @PermitirLectura
    public ResponseEntity<?> listarPqrsUsuario() {
        try {
            return ResponseEntity.ok(pqrsService.listarPqrsUsuario());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sin-asignar")
    @PermitirLectura
    public ResponseEntity<?> listarPqrsSinAsignar() {
        try {
            return ResponseEntity.ok(pqrsService.listarPqrsSinAsignar());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/consulta/{numeroRadicado}/{token}")
    public ResponseEntity<?> consultarPqrs(
            @PathVariable String numeroRadicado,
            @PathVariable String token) {
        try {
            return ResponseEntity.ok(pqrsService.consultarPqrsPublico(numeroRadicado, token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/consulta/token/{token}")
    @PermitirLectura
    public ResponseEntity<?> consultarPqrsPorToken(@PathVariable String token) {
        try {
            return ResponseEntity.ok(pqrsService.consultarPqrsPorToken(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/respuesta/{numeroRadicado}/{token}")
    public ResponseEntity<?> responderSeguimiento(
            @PathVariable String numeroRadicado,
            @PathVariable String token,
            @Valid @RequestBody RespuestaSolicitanteDTO dto) {
        try {
            return ResponseEntity.ok(pqrsService.agregarRespuestaSolicitante(numeroRadicado, token, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/respuesta-usuario")
    @PermitirEscritura
    public ResponseEntity<?> responderPqrsUsuario(
            @PathVariable Long id,
            @RequestParam("comentario") String comentario,
            @RequestParam(value = "archivoAdjunto", required = false) MultipartFile archivo) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            
            // Crear el DTO con los datos recibidos
            CrearSeguimientoDTO dto = new CrearSeguimientoDTO();
            dto.setComentario(comentario);
            
            // Si hay archivo, procesarlo
            if (archivo != null && !archivo.isEmpty()) {
                String nombreArchivo = fileStorageService.storeFile(archivo);
                dto.setArchivoAdjunto(nombreArchivo);
            }

            return ResponseEntity.ok(pqrsService.agregarRespuestaUsuario(id, username, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/historial-asignaciones/{idPqrs}")
    @PermitirLectura
    public ResponseEntity<?> obtenerHistorialPorPqrs(@PathVariable Long idPqrs) {
        try {
            log.debug("Recibida solicitud de historial para PQRS ID: {}", idPqrs);
            List<HistorialAsignacionDTO> historial = historialService.obtenerHistorialPorPqrs(idPqrs);
            
            if (historial.isEmpty()) {
                log.info("No se encontró historial para PQRS ID: {}", idPqrs);
                return ResponseEntity.ok(Collections.emptyList());
            }
            
            return ResponseEntity.ok(historial);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al obtener historial: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener historial: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener el historial de asignaciones"));
        }
    }

    @GetMapping("/estadisticas/dashboard")
    @PermitirLectura
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> statsMap = new HashMap<>();
            statsMap.put("pqrs", pqrsService.getDashboardStats());
            statsMap.put("usuarios", usuarioService.getDashboardStats());
            return ResponseEntity.ok(statsMap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/estadisticas/reportes")
    @PermitirLectura
    public ResponseEntity<Map<String, Object>> getReportes(
    @RequestParam LocalDateTime fechaInicio,
    @RequestParam LocalDateTime fechaFin) {
    
    Map<String, Object> report = new HashMap<>();
    
    // Total PQRS en el período
    Long totalPqrs = pqrsRepository.countByFechaCreacionBetween(fechaInicio, fechaFin);
    report.put("totalPqrs", totalPqrs);
    
    // PQRS por estado
    List<Object[]> porEstado = pqrsRepository.countByEstadoFechas(fechaInicio, fechaFin);
    report.put("porEstado", porEstado.stream()
        .map(row -> Map.of(
            "estado", row[0],
            "cantidad", row[1]
        ))
        .collect(Collectors.toList()));
    
    // PQRS por prioridad
    List<Object[]> porPrioridad = pqrsRepository.countByPrioridadFechas(fechaInicio, fechaFin);
    report.put("porPrioridad", porPrioridad.stream()
        .map(row -> Map.of(
            "prioridad", row[0],
            "cantidad", row[1]
        ))
        .collect(Collectors.toList()));
    
    // PQRS por tema
    List<Object[]> porTema = pqrsRepository.countByTemaFechas(fechaInicio, fechaFin);
    report.put("porArea", porTema.stream()
        .map(row -> Map.of(
            "area", row[0],
            "cantidad", row[1]
        ))
        .collect(Collectors.toList()));
    
    // Tiempo promedio de respuesta
    Double tiempoPromedio = pqrsRepository.getAverageResponseTime(fechaInicio, fechaFin);
    report.put("tiempoPromedio", tiempoPromedio != null ? tiempoPromedio : 0);
    
    // Tendencia mensual
    List<Object[]> tendencia = pqrsRepository.getTendenciaMensual(fechaInicio, fechaFin);
    report.put("tendenciaMensual", tendencia.stream()
        .map(row -> Map.of(
            "mes", row[0],
            "cantidad", row[1]
        ))
        .collect(Collectors.toList()));
    
    return ResponseEntity.ok(report);
    }


}

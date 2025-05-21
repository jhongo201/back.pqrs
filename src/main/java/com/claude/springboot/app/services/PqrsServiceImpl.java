package com.claude.springboot.app.services;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.claude.springboot.app.dto.AreaResponseDTO;
import com.claude.springboot.app.dto.AsignarPqrsDTO;
import com.claude.springboot.app.dto.CrearPqrsDTO;
import com.claude.springboot.app.dto.CrearPqrsUsuarioRegistradoDTO;
import com.claude.springboot.app.dto.CrearSeguimientoDTO;
import com.claude.springboot.app.dto.PqrsResponseDTO;
import com.claude.springboot.app.dto.RespuestaSolicitanteDTO;
import com.claude.springboot.app.dto.SeguimientoResponseDTO;
import com.claude.springboot.app.dto.TemasPqrsResponseDTO;
import com.claude.springboot.app.dto.UsuarioResponseDTO;
import com.claude.springboot.app.entities.HistorialAsignacion;
import com.claude.springboot.app.entities.Pqrs;
import com.claude.springboot.app.entities.SeguimientoPqrs;
import com.claude.springboot.app.entities.SeguimientoPqrs.TipoSeguimiento;
import com.claude.springboot.app.entities.TemasPqrs;
import com.claude.springboot.app.exceptions.PqrsActivoException;
import com.claude.springboot.app.repositories.HistorialAsignacionRepository;
import com.claude.springboot.app.repositories.PqrsRepository;
import com.claude.springboot.app.repositories.SeguimientoPqrsRepository;
import com.claude.springboot.app.repositories.TemasPqrsRepository;
import com.claude.springboot.app.security.entities.Persona;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.UsuarioRepository;
import com.claude.springboot.app.security.service.EmailService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PqrsServiceImpl implements PqrsService {

    private final PqrsRepository pqrsRepository;
    private final TemasPqrsRepository temasPqrsRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialAsignacionRepository historialRepository;
    private final SeguimientoPqrsRepository seguimientoRepository;
    private final RadicadoGeneratorService radicadoGenerator;
    private final FileStorageService fileStorageService;

    @PersistenceContext
    private EntityManager entityManager;
    private final EmailService emailService;

    @Override
    @Transactional
    public PqrsResponseDTO crearPqrsPublico(CrearPqrsDTO dto) {
        TemasPqrs tema = temasPqrsRepository.findById(dto.getIdTema())
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        Pqrs pqrs = new Pqrs();
        pqrs.setTema(tema);
        pqrs.setNombreSolicitante(dto.getNombreSolicitante());
        pqrs.setEmailSolicitante(dto.getEmailSolicitante());
        pqrs.setTelefonoSolicitante(dto.getTelefonoSolicitante());
        pqrs.setTipoDocumentoSolicitante(dto.getTipoDocumentoSolicitante());
        pqrs.setNumeroDocumentoSolicitante(dto.getNumeroDocumentoSolicitante());
        pqrs.setTitulo(dto.getTitulo());
        pqrs.setDescripcion(dto.getDescripcion());
        pqrs.setPrioridad(dto.getPrioridad());

        // Generar el número de radicado
        pqrs.setNumeroRadicado(radicadoGenerator.generarNumeroRadicado());

        // Obtener usuario actual si está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            pqrs.setUsuarioCreador(auth.getName());
        }

        pqrs = pqrsRepository.save(pqrs);

        // Enviar email al solicitante con el link de consulta
        String linkConsulta = String.format("/pqrs/consulta/%s/%s",
                pqrs.getNumeroRadicado(),
                pqrs.getTokenConsulta());

        emailService.enviarConfirmacionPQRS(
                pqrs.getEmailSolicitante(),
                pqrs.getNumeroRadicado(),
                linkConsulta);

        return convertToResponseDTO(pqrs);
    }

    // Nuevo método para usuarios registrados
    @Override
    @Transactional
    public PqrsResponseDTO crearPqrsUsuarioRegistrado(Long idTema, String titulo, 
            String descripcion, String prioridad, MultipartFile archivo) {
        try {
            // Obtener usuario actual
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            TemasPqrs tema = temasPqrsRepository.findById(idTema)
                    .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

            // Validar PQRS activos
            validarPqrsActivos(usuario.getPersona().getNumeroDocumento(), tema.getArea().getIdArea());

            // Crear el PQRS
            Pqrs pqrs = new Pqrs();
            pqrs.setTema(tema);
            pqrs.setTitulo(titulo);
            pqrs.setDescripcion(descripcion);
            pqrs.setPrioridad(prioridad);

            // Establecer datos del usuario
            pqrs.setNombreSolicitante(usuario.getPersona().getNombreCompleto());
            pqrs.setEmailSolicitante(usuario.getPersona().getEmail());
            pqrs.setTelefonoSolicitante(usuario.getPersona().getTelefono());
            pqrs.setTipoDocumentoSolicitante(usuario.getPersona().getTipoDocumento());
            pqrs.setNumeroDocumentoSolicitante(usuario.getPersona().getNumeroDocumento());
            pqrs.setUsuarioCreador(username);

            // Generar número de radicado
            pqrs.setNumeroRadicado(radicadoGenerator.generarNumeroRadicado());
            pqrs.setTokenConsulta(UUID.randomUUID().toString());

            pqrs = pqrsRepository.save(pqrs);

            // Manejar archivo adjunto si existe
            if (archivo != null && !archivo.isEmpty()) {
                String filePath = fileStorageService.storeFile(archivo);
                
                SeguimientoPqrs seguimiento = new SeguimientoPqrs();
                seguimiento.setPqrs(pqrs);
                seguimiento.setUsuario(usuario);
                seguimiento.setComentario("Archivo adjunto en la creación del PQRS");
                seguimiento.setArchivoAdjunto(filePath);
                seguimiento.setTipoSeguimiento(TipoSeguimiento.ADJUNTO_INICIAL);
                seguimiento.setFechaCreacion(LocalDateTime.now());
                seguimientoRepository.save(seguimiento);
            }

            // Enviar correo de confirmación
            emailService.enviarCorreoConfirmacionPqrsAUsuarioRegistrado(pqrs, usuario);

            return convertToResponseDTO(pqrs);
        } catch (PqrsActivoException e) {
            log.error("PQRS activo encontrado: {}", e.getMessage());
            throw e; // Re-lanzar la excepción para mantener el mensaje específico
        } catch (Exception e) {
            log.error("Error al crear PQRS: {}", e.getMessage());
            throw new RuntimeException("Error al crear PQRS: " + e.getMessage());
        }
    }

    private void validarPqrsActivos(String numeroDocumento, Long idArea) {
        List<String> estadosFinalizados = Arrays.asList("RESUELTO", "CERRADO");
        
        if (pqrsRepository.hasActivePqrs(
                numeroDocumento,
                idArea,
                estadosFinalizados)) {
            
            throw new PqrsActivoException(
                String.format("Ya existe una PQRS activa para este número de documento en el área seleccionada")
            );
        }
    }

    @Override
    @Transactional
    public PqrsResponseDTO asignar(Long idPqrs, AsignarPqrsDTO dto) {
        Pqrs pqrs = pqrsRepository.findById(idPqrs)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrado"));

        Usuario usuarioNuevo = usuarioRepository.findById(dto.getIdUsuarioNuevo())
                .orElseThrow(() -> new RuntimeException("Usuario nuevo no encontrado"));

        // Crear historial
        HistorialAsignacion historial = new HistorialAsignacion();
        historial.setPqrs(pqrs);
        historial.setUsuarioAnterior(pqrs.getUsuarioAsignado());
        historial.setUsuarioNuevo(usuarioNuevo);
        historial.setMotivoCambio(dto.getMotivoCambio());
        historialRepository.save(historial);

        // Actualizar PQRS
        pqrs.setUsuarioAsignado(usuarioNuevo);
        pqrs = pqrsRepository.save(pqrs);

        return convertToResponseDTO(pqrs);
    }

    @Override
    @Transactional
    public PqrsResponseDTO agregarSeguimiento(Long idPqrs, String comentario, boolean esRespuestaFinal, MultipartFile archivo) {
        try {
            // Obtener la PQRS
            Pqrs pqrs = pqrsRepository.findById(idPqrs)
                    .orElseThrow(() -> new RuntimeException("PQRS no encontrado"));

            // Obtener usuario actual
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Crear el seguimiento
            SeguimientoPqrs seguimiento = new SeguimientoPqrs();
            seguimiento.setPqrs(pqrs);
            seguimiento.setUsuario(usuario);
            seguimiento.setComentario(comentario);
            seguimiento.setEsRespuestaFinal(esRespuestaFinal);
            seguimiento.setFechaCreacion(LocalDateTime.now());

            // Manejar archivo si existe
            if (archivo != null && !archivo.isEmpty()) {
                String filePath = fileStorageService.storeFile(archivo);
                seguimiento.setArchivoAdjunto(filePath);
            }

            seguimientoRepository.save(seguimiento);

            // Actualizar fecha de última actualización de la PQRS
            pqrs.setFechaUltimaActualizacion(LocalDateTime.now());
            if (esRespuestaFinal) {
                pqrs.setEstadoPqrs("RESUELTO");
            }
            pqrs = pqrsRepository.save(pqrs);

            // Enviar notificación por correo
            String tipoActualizacion = esRespuestaFinal ? "Respuesta Final" : "Nuevo Seguimiento";
            emailService.notificarNuevoSeguimiento(pqrs, tipoActualizacion);

            return convertToResponseDTO(pqrs);
        } catch (Exception e) {
            log.error("Error al agregar seguimiento: {}", e.getMessage());
            throw new RuntimeException("Error al agregar seguimiento: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PqrsResponseDTO actualizarEstado(Long idPqrs, String nuevoEstado) {
        Pqrs pqrs = pqrsRepository.findById(idPqrs)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrado"));

        pqrs.setEstadoPqrs(nuevoEstado);
        pqrs = pqrsRepository.save(pqrs);

        return convertToResponseDTO(pqrs);
    }

    @Override
    @Transactional(readOnly = true)
    public PqrsResponseDTO obtenerPorId(Long id) {
        Pqrs pqrs = pqrsRepository.findByIdWithSeguimientos(id)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrado"));
        return convertToResponseDTO(pqrs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PqrsResponseDTO> listarTodos() {
        try {
            return pqrsRepository.findAllByOrderByIdPqrsDesc().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error al listar todas las PQRS: ", e);
            throw new RuntimeException("Error al obtener la lista de PQRS: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PqrsResponseDTO> listarPorUsuarioAsignado(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return pqrsRepository.findByUsuarioAsignado(usuario).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PqrsResponseDTO> listarPorEstado(String estado) {
        return pqrsRepository.findByEstadoPqrs(estado).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private PqrsResponseDTO convertToResponseDTO(Pqrs pqrs) {
        PqrsResponseDTO dto = new PqrsResponseDTO();
        dto.setIdPqrs(pqrs.getIdPqrs());
        dto.setNumeroRadicado(pqrs.getNumeroRadicado());
        dto.setNombreSolicitante(pqrs.getNombreSolicitante());
        dto.setEmailSolicitante(pqrs.getEmailSolicitante());
        dto.setTelefonoSolicitante(pqrs.getTelefonoSolicitante());
        dto.setTipoDocumentoSolicitante(pqrs.getTipoDocumentoSolicitante());
        dto.setNumeroDocumentoSolicitante(pqrs.getNumeroDocumentoSolicitante());
        dto.setTitulo(pqrs.getTitulo());
        dto.setDescripcion(pqrs.getDescripcion());
        dto.setPrioridad(pqrs.getPrioridad());
        dto.setEstadoPqrs(pqrs.getEstadoPqrs());
        dto.setFechaCreacion(pqrs.getFechaCreacion());
        dto.setFechaUltimaActualizacion(pqrs.getFechaUltimaActualizacion());

        if (pqrs.getTema() != null) {
            dto.setTema(convertToTemaDTO(pqrs.getTema()));
        }

        if (pqrs.getUsuarioAsignado() != null) {
            Usuario usuario = pqrs.getUsuarioAsignado();
            dto.setUsuarioAsignado(new UsuarioResponseDTO(
                pqrs.getUsuarioAsignado().getIdUsuario(),
                pqrs.getUsuarioAsignado().getUsername(),
                usuario.getPersona() != null ? usuario.getPersona().getNombreCompleto() : usuario.getUsername(),
                usuario.getPersona() != null ? usuario.getPersona().getEmail() : null
            ));
        }
    
        List<SeguimientoResponseDTO> seguimientos = pqrs.getSeguimientos().stream()
            .map(this::convertSeguimientoToDTO)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(SeguimientoResponseDTO::getFechaCreacion).reversed())
            .collect(Collectors.toList());
    
        dto.setSeguimientos(seguimientos);

        return dto;
    }

    private UsuarioResponseDTO convertToUsuarioDTO(Usuario usuario) {
    if (usuario == null) {
        return new UsuarioResponseDTO(null, "Usuario no disponible", "Nombre no disponible", "Email no disponible");
    }

    String nombreCompleto = "Nombre no disponible";
    String email = "Email no disponible";

    try {
        if (usuario.getPersona() != null) {
            Persona persona = entityManager.find(Persona.class, usuario.getPersona().getIdPersona());
            if (persona != null) {
                nombreCompleto = persona.getNombres() + " " + persona.getApellidos();
                email = persona.getEmail();
            }
        }
    } catch (Exception e) {
        log.error("Error al acceder a los datos de Persona para el usuario {}: {}", usuario.getIdUsuario(), e.getMessage());
    }

    return new UsuarioResponseDTO(
            usuario.getIdUsuario(),
            usuario.getUsername(),
            nombreCompleto,
            email);
}

private SeguimientoResponseDTO convertSeguimientoToDTO(SeguimientoPqrs seguimiento) {
    if (seguimiento == null) {
        return null;
    }
   
    UsuarioResponseDTO usuarioDTO = null;
    if (seguimiento.getUsuario() != null) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsernameWithRolAndPersona(seguimiento.getUsuario().getUsername());
        
        String nombreMostrado;
        String emailMostrado;
        
        if (usuarioOptional.isPresent() && usuarioOptional.get().getPersona() != null) {
            nombreMostrado = usuarioOptional.get().getPersona().getNombreCompleto();
            emailMostrado = usuarioOptional.get().getPersona().getEmail();
        } else {
            nombreMostrado = seguimiento.getUsuario().getUsername();
            emailMostrado = seguimiento.getUsuario().getUsername() + "@mintrabajo.gov.co";
        }

        usuarioDTO = new UsuarioResponseDTO(
            seguimiento.getUsuario().getIdUsuario(),
            seguimiento.getUsuario().getUsername(),
            nombreMostrado,
            emailMostrado
        );
    }

    // Manejar el caso donde tipoSeguimiento es null
    TipoSeguimiento tipo = seguimiento.getTipoSeguimiento();
    if (tipo == null) {
        tipo = TipoSeguimiento.FUNCIONARIO; // Valor por defecto
    }

    return new SeguimientoResponseDTO(
        seguimiento.getIdSeguimiento(),
        usuarioDTO,
        seguimiento.getComentario(),
        seguimiento.getArchivoAdjunto(),
        seguimiento.isEsRespuestaFinal(),
        seguimiento.getFechaCreacion(),
        tipo.toString() // Ahora es seguro llamar toString()
    );
}

    private TemasPqrsResponseDTO convertToTemaDTO(TemasPqrs tema) {
        TemasPqrsResponseDTO dto = new TemasPqrsResponseDTO();
        dto.setIdTema(tema.getIdTema());
        dto.setNombre(tema.getNombre());
        dto.setDescripcion(tema.getDescripcion());

        if (tema.getArea() != null) {
            AreaResponseDTO areaDTO = new AreaResponseDTO(
                    tema.getArea().getIdArea(),
                    tema.getArea().getNombre());
            dto.setArea(areaDTO);
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public PqrsResponseDTO consultarPorRadicado(String numeroRadicado) {
        Pqrs pqrs = pqrsRepository.findByNumeroRadicado(numeroRadicado)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrado"));
        return convertToResponseDTO(pqrs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PqrsResponseDTO> listarPqrsUsuario() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return pqrsRepository.findByUsuarioCreadorOrderByIdPqrsDesc(username).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PqrsResponseDTO> listarPqrsSinAsignar() {
        return pqrsRepository.findByUsuarioAsignadoIsNullOrderByIdPqrsDesc().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public Long obtenerSiguienteSecuencial() {
        LocalDateTime now = LocalDateTime.now();
        String prefijo = String.format("PQRS-%d-%02d", now.getYear(), now.getMonthValue());
        return pqrsRepository.findLastSecuencial(prefijo) + 1;
    }

    @Override
    @Transactional(readOnly = true)
    public PqrsResponseDTO consultarPqrsPublico(String numeroRadicado, String token) {
        Pqrs pqrs = pqrsRepository.findByNumeroRadicadoAndTokenConsulta(numeroRadicado, token)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrado o token inválido"));
        return convertToResponseDTO(pqrs);
    }

    public PqrsResponseDTO consultarPqrsPorToken(String token) {
        Pqrs pqrs = pqrsRepository.findByTokenConsulta(token)
            .orElseThrow(() -> new RuntimeException("PQRS no encontrado"));
        return convertToResponseDTO(pqrs);
    }

    @Override
    @Transactional
    public PqrsResponseDTO agregarRespuestaSolicitante(String numeroRadicado, String token,
            RespuestaSolicitanteDTO dto) {
        Pqrs pqrs = pqrsRepository.findByNumeroRadicadoAndTokenConsulta(numeroRadicado, token)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrado o token inválido"));

        SeguimientoPqrs seguimiento = new SeguimientoPqrs();
        seguimiento.setPqrs(pqrs);
        seguimiento.setComentario(dto.getComentario());
        seguimiento.setArchivoAdjunto(dto.getArchivoAdjunto());
        seguimiento.setTipoSeguimiento(TipoSeguimiento.RESPUESTA_SOLICITANTE);
        seguimiento.setFechaCreacion(LocalDateTime.now());

        seguimientoRepository.save(seguimiento);

        // Notificar al funcionario asignado
        if (pqrs.getUsuarioAsignado() != null) {
            emailService.notificarNuevaRespuestaSolicitante(pqrs);
        }

        return convertToResponseDTO(pqrs);
    }

    @Override
    @Transactional
    public PqrsResponseDTO agregarRespuestaUsuario(Long idPqrs, String username, CrearSeguimientoDTO dto) {
        Pqrs pqrs = pqrsRepository.findById(idPqrs)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrado"));

        // Obtener el usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear el seguimiento
        SeguimientoPqrs seguimiento = new SeguimientoPqrs();
        seguimiento.setPqrs(pqrs);
        seguimiento.setComentario(dto.getComentario());
        seguimiento.setArchivoAdjunto(dto.getArchivoAdjunto());
        seguimiento.setUsuario(usuario);
        seguimiento.setTipoSeguimiento(TipoSeguimiento.RESPUESTA_USUARIO);
        seguimiento.setEsRespuestaFinal(false);

        seguimientoRepository.save(seguimiento);

        // Actualizar la fecha de última actualización del PQRS
        pqrs.setFechaUltimaActualizacion(LocalDateTime.now());
        pqrsRepository.save(pqrs);

        return convertToResponseDTO(pqrs);
    }

    @Override
    @Transactional
    public PqrsResponseDTO actualizarPrioridad(Long idPqrs, String nuevoEstado) {
        Pqrs pqrs = pqrsRepository.findById(idPqrs)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrado"));

        pqrs.setPrioridad(nuevoEstado);
        pqrs = pqrsRepository.save(pqrs);

        return convertToResponseDTO(pqrs);
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);  

        LocalDateTime startOfWeek = now.minusDays(7);
        LocalDateTime startOfMonth = now.withDayOfMonth(1);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

        // // Semana actual:
        LocalDateTime startOfCurrentWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfCurrentWeek = now;

        // Ajuste de fechas para semana anterior
        LocalDateTime startOfPreviousWeek = startOfCurrentWeek.minusWeeks(1);
        LocalDateTime endOfPreviousWeek = startOfCurrentWeek.minusNanos(1);
        
        log.info("Fechas semana actual: {} - {}", startOfCurrentWeek, endOfCurrentWeek);
        log.info("Fechas semana actual: {} - {}", startOfCurrentWeek, now);
        log.info("Fechas semana anterior: {} - {}", startOfPreviousWeek, endOfPreviousWeek);

        System.out.println("Fechas semana actual: " + startOfCurrentWeek + " - " + endOfCurrentWeek);
        System.out.println("Fechas semana anterior: " + startOfPreviousWeek + " - " + endOfPreviousWeek);
         // Total estados y prioridad
        stats.put("porEstado", convertToMap(pqrsRepository.countByEstado()));
        stats.put("porPrioridad", convertToMap(pqrsRepository.countByPrioridad()));
    
        // Estados por período
        stats.put("estadosDia", convertToMap(pqrsRepository.countByEstadoAndDateBetween(startOfDay, endOfDay)));
        stats.put("estadosHoy", convertToMap(pqrsRepository.countByEstadoAndDateBetween(startOfCurrentWeek, endOfCurrentWeek)));
        stats.put("estadosSemana", convertToMap(pqrsRepository.countByEstadoAndDateBetween(startOfPreviousWeek, endOfPreviousWeek)));
        stats.put("estadosMes", convertToMap(pqrsRepository.countByEstadoAndDateBetween(startOfMonth, endOfMonth)));
    
        // Prioridades por período
        //stats.put("prioridadesHoy", convertToMap(pqrsRepository.countByPrioridadAndDateAfter(startOfDay)));
        //stats.put("prioridadesSemana", convertToMap(pqrsRepository.countByPrioridadAndDateAfter(startOfWeek)));
        //stats.put("prioridadesMes", convertToMap(pqrsRepository.countByPrioridadAndDateAfter(startOfMonth)));
        stats.put("prioridadesHoy", convertToMap(pqrsRepository.countByPrioridadAndDateBetween(startOfDay, endOfDay)));
        stats.put("prioridadesSemana", convertToMap(pqrsRepository.countByPrioridadAndDateBetween(startOfCurrentWeek, endOfCurrentWeek)));
        stats.put("prioridadesMes", convertToMap(pqrsRepository.countByPrioridadAndDateBetween(startOfMonth, endOfMonth)));
    
        return stats;
    }
    
    private Map<String, Long> convertToMap(List<Object[]> results) {
        Map<String, Long> map = new HashMap<>();
        for (Object[] result : results) {
            map.put((String)result[0], (Long)result[1]);
        }
        return map;
    }

    

    

}

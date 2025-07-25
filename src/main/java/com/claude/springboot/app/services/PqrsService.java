package com.claude.springboot.app.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.claude.springboot.app.dto.AsignarPqrsDTO;
import com.claude.springboot.app.dto.CrearPqrsDTO;
import com.claude.springboot.app.dto.CrearPqrsUsuarioRegistradoDTO;
import com.claude.springboot.app.dto.CrearSeguimientoDTO;
import com.claude.springboot.app.dto.PqrsResponseDTO;
import com.claude.springboot.app.dto.RespuestaSolicitanteDTO;

public interface PqrsService {
    PqrsResponseDTO crearPqrsPublico(CrearPqrsDTO dto, MultipartFile archivo);
    PqrsResponseDTO crearPqrsUsuarioRegistrado(Long idTema, String titulo, 
        String descripcion, String prioridad, MultipartFile archivo);
    PqrsResponseDTO asignar(Long idPqrs, AsignarPqrsDTO dto);
    PqrsResponseDTO agregarSeguimiento(Long idPqrs, String comentario, boolean esRespuestaFinal, MultipartFile archivo);
    PqrsResponseDTO actualizarEstado(Long idPqrs, String nuevoEstado);
    PqrsResponseDTO obtenerPorId(Long id);
    List<PqrsResponseDTO> listarTodos();
    Page<PqrsResponseDTO> listarTodos(Pageable pageable);
    List<PqrsResponseDTO> listarPorUsuarioAsignado(Long idUsuario);
    List<PqrsResponseDTO> listarPorEstado(String estado);
    PqrsResponseDTO consultarPorRadicado(String numeroRadicado);
    List<PqrsResponseDTO> listarPqrsUsuario();
    Page<PqrsResponseDTO> listarPqrsUsuario(Pageable pageable);
    List<PqrsResponseDTO> listarPqrsSinAsignar();
    Page<PqrsResponseDTO> listarPqrsSinAsignar(Pageable pageable);
    Long obtenerSiguienteSecuencial();
    PqrsResponseDTO consultarPqrsPublico(String numeroRadicado, String token);
    PqrsResponseDTO agregarRespuestaSolicitante(String numeroRadicado, String token, RespuestaSolicitanteDTO dto);
    PqrsResponseDTO agregarRespuestaUsuario(Long idPqrs, String username, CrearSeguimientoDTO dto);
    PqrsResponseDTO actualizarPrioridad(Long idPqrs, String nuevaPrioridad);
    PqrsResponseDTO consultarPqrsPorToken(String token);
    Map<String, Object> getDashboardStats();
    
}

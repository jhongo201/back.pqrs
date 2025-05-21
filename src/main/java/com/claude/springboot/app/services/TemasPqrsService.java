package com.claude.springboot.app.services;

import java.util.List;

import com.claude.springboot.app.dto.ActualizarResponsableDTO;
import com.claude.springboot.app.dto.TemasPqrsDTO;
import com.claude.springboot.app.dto.TemasPqrsResponseDTO;
import com.claude.springboot.app.dto.VincularResponsableDTO;

public interface TemasPqrsService {
    TemasPqrsResponseDTO crear(TemasPqrsDTO dto);
    TemasPqrsResponseDTO actualizar(Long id, TemasPqrsDTO dto);
    void eliminar(Long id);
    TemasPqrsResponseDTO obtenerPorId(Long id);
    List<TemasPqrsResponseDTO> listarTodos();
    TemasPqrsResponseDTO vincularResponsable(Long idTema, VincularResponsableDTO dto);
    TemasPqrsResponseDTO desvincularResponsable(Long idTema, Long idUsuario);
    TemasPqrsResponseDTO actualizarResponsable(Long idTema, ActualizarResponsableDTO dto);
}

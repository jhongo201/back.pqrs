package com.claude.springboot.app.security.service;

import java.util.List;

import com.claude.springboot.app.security.dto.DireccionDTO;

public interface DireccionService {
    List<DireccionDTO> listarTodos();
    DireccionDTO obtenerPorId(Long id);
    DireccionDTO crear(DireccionDTO direccionDTO);
    DireccionDTO actualizar(Long id, DireccionDTO direccionDTO);
    void eliminar(Long id);
 }

package com.claude.springboot.app.security.service;

import java.util.List;

import com.claude.springboot.app.security.dto.TerritorialDTO;

public interface TerritorialService {
    List<TerritorialDTO> listarTodos();
    TerritorialDTO obtenerPorId(Long id);
    TerritorialDTO crear(TerritorialDTO territorialDTO);
    TerritorialDTO actualizar(Long id, TerritorialDTO territorialDTO);
    void eliminar(Long id);
 }

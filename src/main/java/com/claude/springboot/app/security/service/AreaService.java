package com.claude.springboot.app.security.service;

import java.util.List;

import com.claude.springboot.app.security.dto.AreaDTO;

public interface AreaService {
    List<AreaDTO> listarTodos();
    AreaDTO obtenerPorId(Long id);
    AreaDTO crear(AreaDTO areaDTO);
    AreaDTO actualizar(Long id, AreaDTO areaDTO);
    void eliminar(Long id);
 }

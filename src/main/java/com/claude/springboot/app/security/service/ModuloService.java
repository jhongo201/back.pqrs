package com.claude.springboot.app.security.service;

import java.util.List;

import com.claude.springboot.app.security.dto.ModuloDTO;

public interface ModuloService {
    List<ModuloDTO> listarTodos();
    ModuloDTO obtenerPorId(Long id);
    ModuloDTO crear(ModuloDTO moduloDTO);
    ModuloDTO actualizar(Long id, ModuloDTO moduloDTO);
    void eliminar(Long id);
}


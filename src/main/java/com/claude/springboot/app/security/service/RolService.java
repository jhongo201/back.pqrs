package com.claude.springboot.app.security.service;

import java.util.List;

import com.claude.springboot.app.security.dto.RolDTO;

public interface RolService {
    List<RolDTO> listarTodos();
    RolDTO obtenerPorId(Long id);
    RolDTO crear(RolDTO rolDTO);
    RolDTO actualizar(Long id, RolDTO rolDTO);
    void eliminar(Long id);
}

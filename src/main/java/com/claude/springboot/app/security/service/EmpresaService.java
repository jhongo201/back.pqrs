package com.claude.springboot.app.security.service;

import java.util.List;

import com.claude.springboot.app.security.dto.EmpresaDTO;

public interface EmpresaService {
    List<EmpresaDTO> listarTodos();
    EmpresaDTO obtenerPorId(Long id);
    EmpresaDTO crear(EmpresaDTO empresaDTO);
    EmpresaDTO actualizar(Long id, EmpresaDTO empresaDTO);
    void eliminar(Long id);
 }
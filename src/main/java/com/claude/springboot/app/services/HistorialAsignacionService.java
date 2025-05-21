package com.claude.springboot.app.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.claude.springboot.app.dto.HistorialAsignacionDTO;

@Service
public interface HistorialAsignacionService {
    List<HistorialAsignacionDTO> obtenerHistorialPorPqrs(Long idPqrs);
}

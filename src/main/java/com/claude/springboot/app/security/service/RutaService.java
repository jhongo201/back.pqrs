package com.claude.springboot.app.security.service;

import java.util.List;

import com.claude.springboot.app.security.dto.ActualizarRutaDTO;
import com.claude.springboot.app.security.dto.PermisoRolRutaResponseDTO;
import com.claude.springboot.app.security.dto.RegistroRutaDTO;
import com.claude.springboot.app.security.dto.RutaResponseDTO;
import com.claude.springboot.app.security.entities.PermisoRol;
import com.claude.springboot.app.security.entities.Ruta;


public interface RutaService {

    public List<String> obtenerRutasPublicas();
    RutaResponseDTO registrarRuta(RegistroRutaDTO registroDTO);
    List<RutaResponseDTO> listarRutas();
    RutaResponseDTO obtenerRuta(Long id);
    RutaResponseDTO actualizarRuta(ActualizarRutaDTO actualizarDTO);
    void eliminarRuta(Long id);
    RutaResponseDTO mapToRutaResponseDTO(Ruta ruta);
    PermisoRolRutaResponseDTO mapToPermisoRolResponseDTO(PermisoRol permiso);
}

package com.claude.springboot.app.security.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaResponseDTO {
    private Long idRuta;
    private Long idModulo;
    private String nombreModulo;
    private String ruta;
    private String descripcion;
    private boolean estado;
    private boolean esPublica;
    private LocalDateTime fechaCreacion;
    private List<PermisoRolRutaResponseDTO> permisos;
    
}

package com.claude.springboot.app.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoRolRutaResponseDTO {
    private Long idPermiso;
    private Long idRol;
    private String nombreRol;
    private boolean puedeLeer;
    private boolean puedeEscribir;
    private boolean puedeEliminar;
    private boolean puedeActualizar;
    private boolean estado;
}

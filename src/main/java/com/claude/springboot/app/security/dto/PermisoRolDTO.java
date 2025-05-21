package com.claude.springboot.app.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoRolDTO {
    private Long idPermiso;
    private Long idRol;
    private Long idRuta;
    private boolean puedeLeer;
    private boolean puedeEscribir;
    private boolean puedeActualizar;
    private boolean puedeEliminar;
    private boolean estado = true;
}


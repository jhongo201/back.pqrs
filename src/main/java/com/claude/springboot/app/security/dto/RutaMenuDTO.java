package com.claude.springboot.app.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutaMenuDTO {
    private Long id;
    private String ruta;
    private String descripcion;
    private boolean puedeLeer;
    private boolean puedeEscribir;
    private boolean puedeActualizar;
    private boolean puedeEliminar;
}

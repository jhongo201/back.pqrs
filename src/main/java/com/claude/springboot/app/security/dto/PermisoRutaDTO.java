package com.claude.springboot.app.security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoRutaDTO {
    @NotNull(message = "El ID de la ruta es obligatorio")
    private Long idRuta;
    private boolean puedeLeer;
    private boolean puedeEscribir;
    private boolean puedeActualizar;
    private boolean puedeEliminar;
}

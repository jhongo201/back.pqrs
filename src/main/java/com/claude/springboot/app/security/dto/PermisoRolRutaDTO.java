package com.claude.springboot.app.security.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermisoRolRutaDTO {
    @NotNull(message = "El id del rol es requerido")
    private Long idRol;
    
    @NotNull(message = "Debe especificar si puede leer")
    private Boolean puedeLeer;
    
    @NotNull(message = "Debe especificar si puede escribir")
    private Boolean puedeEscribir;
    
    @NotNull(message = "Debe especificar si puede eliminar")
    private Boolean puedeEliminar;
    
    @NotNull(message = "Debe especificar si puede actualizar")
    private Boolean puedeActualizar;
}

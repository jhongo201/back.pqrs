package com.claude.springboot.app.security.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para asignación masiva de permisos a un rol
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionPermisosDTO {
    @NotNull(message = "El ID del rol es obligatorio")
    private Long idRol;
    
    @NotNull(message = "La lista de permisos es obligatoria")
    private List<PermisoRutaDTO> permisos;
}

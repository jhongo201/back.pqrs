package com.claude.springboot.app.security.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarRutaDTO {
    @NotNull(message = "El id de la ruta es requerido")
    private Long idRuta;
    
    @NotNull(message = "El id del módulo es requerido")
    private Long idModulo;
    
    @NotEmpty(message = "La ruta es requerida")
    private String ruta;
    
    @NotEmpty(message = "La descripción es requerida")
    private String descripcion;
    
    @NotNull(message = "El estado es requerido")
    private Boolean estado;
    
    @NotNull(message = "Debe especificar si la ruta es pública")
    private Boolean esPublica;
    
    private List<PermisoRolRutaDTO> permisosRol;
}

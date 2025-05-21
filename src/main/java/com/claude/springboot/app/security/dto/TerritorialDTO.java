package com.claude.springboot.app.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TerritorialDTO {
    private Long idTerritorial;
    private Long idEmpresa;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El código es obligatorio")
    private String codigo;
    
    private String descripcion;
    private boolean estado = true;
}

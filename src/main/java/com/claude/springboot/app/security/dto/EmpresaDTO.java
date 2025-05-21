package com.claude.springboot.app.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {
    private Long idEmpresa;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El NIT es obligatorio")
    private String nit;
    
    private String telefono;
    private String email;
    private boolean estado = true;
}

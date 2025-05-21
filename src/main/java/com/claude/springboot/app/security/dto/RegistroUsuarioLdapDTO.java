package com.claude.springboot.app.security.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioLdapDTO {
    @NotEmpty(message = "El username es requerido")
    private String username;
    
    @NotNull(message = "El id del rol es requerido")
    private Long idRol;
    
    @NotNull(message = "El estado es requerido")
    private Boolean estado;
}

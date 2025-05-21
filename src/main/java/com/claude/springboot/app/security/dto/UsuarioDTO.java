package com.claude.springboot.app.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    
    @NotBlank(message = "El username es obligatorio")
    private String username;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    @NotNull(message = "El ID de persona es obligatorio")
    private Long idPersona;
    
    @NotNull(message = "El ID de rol es obligatorio")
    private Long idRol;
    
    private boolean estado = true;
}

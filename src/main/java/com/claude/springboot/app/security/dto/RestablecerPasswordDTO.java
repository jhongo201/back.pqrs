package com.claude.springboot.app.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestablecerPasswordDTO {
    
    @NotBlank(message = "El token es requerido")
    private String token;
    
    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String nuevaPassword;
    
    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String confirmarPassword;
}

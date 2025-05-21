package com.claude.springboot.app.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioDTO {
    // Datos de la persona
    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;
    
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;
    
    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumento;
    
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    private String telefono;

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long idEmpresa;
    
    @NotNull(message = "El ID del área es obligatorio")
    private Long idArea;
    
    // Datos del usuario
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    @NotNull(message = "El ID del rol es obligatorio")
    private Long idRol;
}

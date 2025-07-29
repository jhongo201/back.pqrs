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
    
    // Campos mínimos requeridos para crear la Persona asociada
    @NotEmpty(message = "El primer nombre es requerido")
    private String primerNombre;
    
    private String otrosNombres; // Opcional
    
    @NotEmpty(message = "El primer apellido es requerido")
    private String primerApellido;
    
    private String segundoApellido; // Opcional
    
    @NotNull(message = "El área es requerida")
    private Long idArea;
    
    // Campos opcionales (se pueden generar automáticamente o dejar nulos)
    private String email; // Se genera automáticamente desde username si no se proporciona
    private String numeroDocumento; // Opcional para usuarios LDAP
    private Long idTipoDocumento; // Opcional, se asigna tipo por defecto si no se proporciona
    private String telefono;
    private String direccion;
    private Long idMunicipio;
    private Long idEmpresa;
}

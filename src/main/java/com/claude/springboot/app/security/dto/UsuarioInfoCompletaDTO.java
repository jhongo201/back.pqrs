package com.claude.springboot.app.security.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioInfoCompletaDTO {
    private Long idUsuario;
    private String username;
    private boolean estado;
    private LocalDateTime fechaCreacion;
    private String rol;
    // Información de la persona
    private Long idPersona;
    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
    private String email;
    private String telefono;
    private boolean estadoPersona;
    private LocalDateTime fechaCreacionPersona;
    // Información del área
    private Long idArea;
    private String nombreArea;
    private boolean estadoArea;
    // Información de la dirección
    private Long idDireccion;
    private String nombreDireccion;
    private boolean estadoDireccion;
    // Información de la territorial
    private Long idTerritorial;
    private String nombreTerritorial;
    private boolean estadoTerritorial;
    // Información de la empresa
    private Long idEmpresa;
    private String nombreEmpresa;
    private boolean estadoEmpresa;
}

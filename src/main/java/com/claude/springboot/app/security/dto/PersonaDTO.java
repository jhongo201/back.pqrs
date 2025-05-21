package com.claude.springboot.app.security.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDTO {

    private Long idPersona;
    private Long idEmpresa; // ID de la empresa relacionada
    private Long idArea; // ID del área relacionada
    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
    private String email;
    private String telefono;
    private boolean estado;
    private LocalDateTime fechaCreacion;

    // Nombre completo como campo adicional en el DTO
    private String nombreCompleto;

}

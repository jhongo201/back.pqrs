package com.claude.springboot.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearPqrsDTO {
    @NotEmpty
    private String nombreSolicitante;
    @NotEmpty
    @Email
    private String emailSolicitante;
    private String telefonoSolicitante;
    @NotEmpty
    private String tipoDocumentoSolicitante;
    @NotEmpty
    private String numeroDocumentoSolicitante;
    @NotNull
    private Long idTema;
    @NotEmpty
    private String titulo;
    @NotEmpty
    private String descripcion;
    private String prioridad;
}

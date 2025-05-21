package com.claude.springboot.app.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PqrsResponseDTO {
    private Long idPqrs;
    private String nombreSolicitante;
    private String emailSolicitante;
    private String telefonoSolicitante;
    private String tipoDocumentoSolicitante;
    private String numeroDocumentoSolicitante;
    private TemasPqrsResponseDTO tema;
    private UsuarioResponseDTO usuarioAsignado;
    private String titulo;
    private String descripcion;
    private String prioridad;
    private String estadoPqrs;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaActualizacion;
    private List<SeguimientoResponseDTO> seguimientos;
    private String numeroRadicado;
    
}

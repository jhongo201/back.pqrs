package com.claude.springboot.app.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearPqrsUsuarioRegistradoDTO {
    @NotNull(message = "El id del tema es requerido")
    private Long idTema;
    
    @NotEmpty(message = "El título es requerido")
    private String titulo;
    
    @NotEmpty(message = "La descripción es requerida")
    private String descripcion;
    
    private String prioridad;
    
    private MultipartFile archivoAdjunto;
}

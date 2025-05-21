package com.claude.springboot.app.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemasPqrsDTO {
    @NotNull(message = "El id del área es requerido")
    private Long idArea;
    
    @NotEmpty(message = "El nombre es requerido")
    @Size(max = 100)
    private String nombre;
    
    private String descripcion;
    private Boolean estado;
}

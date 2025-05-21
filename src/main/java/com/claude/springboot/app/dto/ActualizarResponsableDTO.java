package com.claude.springboot.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarResponsableDTO {
    @NotNull(message = "El id del usuario anterior es requerido")
    private Long idUsuarioAnterior;
    
    @NotNull(message = "El id del usuario nuevo es requerido")
    private Long idUsuarioNuevo;
}

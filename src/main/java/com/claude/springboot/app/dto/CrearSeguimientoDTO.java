package com.claude.springboot.app.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearSeguimientoDTO {
    @NotEmpty
    private String comentario;
    private String archivoAdjunto;
    private boolean esRespuestaFinal;
}

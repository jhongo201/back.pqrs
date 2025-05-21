package com.claude.springboot.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VincularResponsableDTO {
    @NotNull(message = "El id del usuario es requerido")
    private Long idUsuario;
}

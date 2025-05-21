package com.claude.springboot.app.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AsignarPqrsDTO {
    @NotNull
    private Long idUsuarioNuevo;
    @NotEmpty
    private String motivoCambio;
}

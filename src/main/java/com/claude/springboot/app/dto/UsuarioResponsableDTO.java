package com.claude.springboot.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponsableDTO {
    private Long idUsuario;
    private String username;
    private String nombreCompleto;
}
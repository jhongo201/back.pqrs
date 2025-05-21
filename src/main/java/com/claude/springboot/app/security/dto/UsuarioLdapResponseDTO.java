package com.claude.springboot.app.security.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLdapResponseDTO {
    private Long idUsuario;
    private String username;
    private boolean estado;
    private LocalDateTime fechaCreacion;
    private RolResponseDTO rol;
}

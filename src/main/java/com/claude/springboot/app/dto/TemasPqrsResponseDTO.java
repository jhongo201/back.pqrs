package com.claude.springboot.app.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemasPqrsResponseDTO {
    private Long idTema;
    private AreaResponseDTO area;
    private String nombre;
    private String descripcion;
    private boolean estado;
    private LocalDateTime fechaCreacion;
    private List<UsuarioResponsableDTO> responsables;
}

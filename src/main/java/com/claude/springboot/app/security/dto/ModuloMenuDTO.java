package com.claude.springboot.app.security.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuloMenuDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private List<RutaMenuDTO> rutas;
}

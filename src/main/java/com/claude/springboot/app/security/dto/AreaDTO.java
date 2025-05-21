package com.claude.springboot.app.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaDTO {
   private Long idArea;
   private Long idDireccion;
   
   @NotBlank(message = "El nombre es obligatorio")
   private String nombre;
   private String descripcion;
   private boolean estado = true;
}

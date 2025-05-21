package com.claude.springboot.app.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoResponseDTO {
   private Long idSeguimiento;
   private UsuarioResponseDTO usuario;
   private String comentario;
   private String archivoAdjunto;
   private boolean esRespuestaFinal;
   private LocalDateTime fechaCreacion;
   private String tipoSeguimiento;  // Asegúrate de que este campo esté aquí
}

package com.claude.springboot.app.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class  HistorialAsignacionDTO{
    private Long idHistorial;
    private String usuarioAnterior;
    private String usuarioNuevo;
    private String areaAnterior;  // Nuevo campo
    private String areaNueva;     // Nuevo campo
    private String motivoCambio;
    private LocalDateTime fechaAsignacion;

    public HistorialAsignacionDTO(Long idHistorial, 
                                String usuarioAnterior, 
                                String usuarioNuevo,
                                String areaAnterior,
                                String areaNueva,
                                String motivoCambio, 
                                LocalDateTime fechaAsignacion) {
        this.idHistorial = idHistorial;
        this.usuarioAnterior = usuarioAnterior;
        this.usuarioNuevo = usuarioNuevo;
        this.areaAnterior = areaAnterior;
        this.areaNueva = areaNueva;
        this.motivoCambio = motivoCambio;
        this.fechaAsignacion = fechaAsignacion;
    }

}

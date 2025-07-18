package com.claude.springboot.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class DepartamentoDTO {
    
    @Size(min = 2, max = 2, message = "El código DANE del departamento debe tener 2 caracteres")
    @NotBlank(message = "El código DANE del departamento es obligatorio")
    private String codigoDane;
    
    @NotBlank(message = "El nombre del departamento es obligatorio")
    @Size(max = 100, message = "El nombre del departamento no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 100, message = "El nombre de la capital no puede exceder 100 caracteres")
    private String capital;
    
    @Size(max = 50, message = "El nombre de la región no puede exceder 50 caracteres")
    private String region;
    
    private LocalDateTime fechaCreacion;
    
    private Boolean activo;
    
    private List<MunicipioDTO> municipios;
    
    private Integer totalMunicipios;
    
    private Long poblacionTotalEstimada;
    
    // Constructores
    public DepartamentoDTO() {}
    
    public DepartamentoDTO(String codigoDane, String nombre) {
        this.codigoDane = codigoDane;
        this.nombre = nombre;
    }
    
    public DepartamentoDTO(String codigoDane, String nombre, String capital, String region) {
        this.codigoDane = codigoDane;
        this.nombre = nombre;
        this.capital = capital;
        this.region = region;
    }
    
    // Getters y Setters
    public String getCodigoDane() {
        return codigoDane;
    }
    
    public void setCodigoDane(String codigoDane) {
        this.codigoDane = codigoDane;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getCapital() {
        return capital;
    }
    
    public void setCapital(String capital) {
        this.capital = capital;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public List<MunicipioDTO> getMunicipios() {
        return municipios;
    }
    
    public void setMunicipios(List<MunicipioDTO> municipios) {
        this.municipios = municipios;
    }
    
    public Integer getTotalMunicipios() {
        return totalMunicipios;
    }
    
    public void setTotalMunicipios(Integer totalMunicipios) {
        this.totalMunicipios = totalMunicipios;
    }
    
    public Long getPoblacionTotalEstimada() {
        return poblacionTotalEstimada;
    }
    
    public void setPoblacionTotalEstimada(Long poblacionTotalEstimada) {
        this.poblacionTotalEstimada = poblacionTotalEstimada;
    }
    
    @Override
    public String toString() {
        return "DepartamentoDTO{" +
                "codigoDane='" + codigoDane + '\'' +
                ", nombre='" + nombre + '\'' +
                ", capital='" + capital + '\'' +
                ", region='" + region + '\'' +
                ", activo=" + activo +
                ", totalMunicipios=" + totalMunicipios +
                '}';
    }
}

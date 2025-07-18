package com.claude.springboot.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

public class MunicipioDTO {
    
    @Size(min = 5, max = 5, message = "El código DANE del municipio debe tener 5 caracteres")
    @NotBlank(message = "El código DANE del municipio es obligatorio")
    private String codigoDane;
    
    @NotBlank(message = "El nombre del municipio es obligatorio")
    @Size(max = 100, message = "El nombre del municipio no puede exceder 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El código del departamento es obligatorio")
    private String codigoDepartamento;
    
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;
    
    @Min(value = 0, message = "La población estimada no puede ser negativa")
    private Integer poblacionEstimada;
    
    private LocalDateTime fechaCreacion;
    
    private Boolean activo;
    
    // Información del departamento (para consultas completas)
    private String nombreDepartamento;
    
    private String capitalDepartamento;
    
    private String region;
    
    // Constructores
    public MunicipioDTO() {}
    
    public MunicipioDTO(String codigoDane, String nombre, String codigoDepartamento) {
        this.codigoDane = codigoDane;
        this.nombre = nombre;
        this.codigoDepartamento = codigoDepartamento;
    }
    
    public MunicipioDTO(String codigoDane, String nombre, String codigoDepartamento, 
                       String categoria, Integer poblacionEstimada) {
        this.codigoDane = codigoDane;
        this.nombre = nombre;
        this.codigoDepartamento = codigoDepartamento;
        this.categoria = categoria;
        this.poblacionEstimada = poblacionEstimada;
    }
    
    // Constructor completo con información del departamento
    public MunicipioDTO(String codigoDane, String nombre, String codigoDepartamento,
                       String categoria, Integer poblacionEstimada, String nombreDepartamento,
                       String capitalDepartamento, String region) {
        this.codigoDane = codigoDane;
        this.nombre = nombre;
        this.codigoDepartamento = codigoDepartamento;
        this.categoria = categoria;
        this.poblacionEstimada = poblacionEstimada;
        this.nombreDepartamento = nombreDepartamento;
        this.capitalDepartamento = capitalDepartamento;
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
    
    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }
    
    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public Integer getPoblacionEstimada() {
        return poblacionEstimada;
    }
    
    public void setPoblacionEstimada(Integer poblacionEstimada) {
        this.poblacionEstimada = poblacionEstimada;
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
    
    public String getNombreDepartamento() {
        return nombreDepartamento;
    }
    
    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }
    
    public String getCapitalDepartamento() {
        return capitalDepartamento;
    }
    
    public void setCapitalDepartamento(String capitalDepartamento) {
        this.capitalDepartamento = capitalDepartamento;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    // Métodos de utilidad
    public String getNombreCompleto() {
        if (nombreDepartamento != null) {
            return nombre + ", " + nombreDepartamento;
        }
        return nombre;
    }
    
    public boolean validarCodigoDane() {
        if (codigoDane != null && codigoDane.length() == 5 && 
            codigoDepartamento != null && codigoDepartamento.length() == 2) {
            return codigoDane.startsWith(codigoDepartamento);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "MunicipioDTO{" +
                "codigoDane='" + codigoDane + '\'' +
                ", nombre='" + nombre + '\'' +
                ", codigoDepartamento='" + codigoDepartamento + '\'' +
                ", categoria='" + categoria + '\'' +
                ", poblacionEstimada=" + poblacionEstimada +
                ", nombreDepartamento='" + nombreDepartamento + '\'' +
                ", activo=" + activo +
                '}';
    }
}

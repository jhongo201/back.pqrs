package com.claude.springboot.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "Municipios", indexes = {
    @Index(name = "IX_Municipios_Departamento", columnList = "codigo_departamento"),
    @Index(name = "IX_Municipios_Nombre", columnList = "nombre")
})
public class Municipio {
    
    @Id
    @Column(name = "codigo_dane", length = 5)
    @Size(min = 5, max = 5, message = "El código DANE del municipio debe tener 5 caracteres")
    @NotBlank(message = "El código DANE del municipio es obligatorio")
    private String codigoDane;
    
    @Column(name = "nombre", length = 100, nullable = false)
    @NotBlank(message = "El nombre del municipio es obligatorio")
    @Size(max = 100, message = "El nombre del municipio no puede exceder 100 caracteres")
    private String nombre;
    
    @Column(name = "codigo_departamento", length = 2, nullable = false)
    @NotBlank(message = "El código del departamento es obligatorio")
    private String codigoDepartamento;
    
    @Column(name = "categoria", length = 50)
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;
    
    @Column(name = "poblacion_estimada")
    @Min(value = 0, message = "La población estimada no puede ser negativa")
    private Integer poblacionEstimada;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    // Relación muchos a uno con Departamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_departamento", referencedColumnName = "codigo_dane", 
                insertable = false, updatable = false)
    private Departamento departamento;
    
    // Constructores
    public Municipio() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    public Municipio(String codigoDane, String nombre, String codigoDepartamento) {
        this();
        this.codigoDane = codigoDane;
        this.nombre = nombre;
        this.codigoDepartamento = codigoDepartamento;
    }
    
    public Municipio(String codigoDane, String nombre, String codigoDepartamento, 
                    String categoria, Integer poblacionEstimada) {
        this(codigoDane, nombre, codigoDepartamento);
        this.categoria = categoria;
        this.poblacionEstimada = poblacionEstimada;
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
    
    public Departamento getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
        if (departamento != null) {
            this.codigoDepartamento = departamento.getCodigoDane();
        }
    }
    
    // Métodos de utilidad
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
    
    /**
     * Método para obtener el nombre completo del municipio con su departamento
     */
    public String getNombreCompleto() {
        if (departamento != null) {
            return nombre + ", " + departamento.getNombre();
        }
        return nombre;
    }
    
    /**
     * Método para validar si el código DANE del municipio corresponde al departamento
     */
    public boolean validarCodigoDane() {
        if (codigoDane != null && codigoDane.length() == 5 && 
            codigoDepartamento != null && codigoDepartamento.length() == 2) {
            return codigoDane.startsWith(codigoDepartamento);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Municipio{" +
                "codigoDane='" + codigoDane + '\'' +
                ", nombre='" + nombre + '\'' +
                ", codigoDepartamento='" + codigoDepartamento + '\'' +
                ", categoria='" + categoria + '\'' +
                ", poblacionEstimada=" + poblacionEstimada +
                ", activo=" + activo +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Municipio municipio = (Municipio) o;
        return codigoDane != null ? codigoDane.equals(municipio.codigoDane) : municipio.codigoDane == null;
    }
    
    @Override
    public int hashCode() {
        return codigoDane != null ? codigoDane.hashCode() : 0;
    }
}

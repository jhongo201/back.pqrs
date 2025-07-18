package com.claude.springboot.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Departamentos", indexes = {
    @Index(name = "IX_Departamentos_Nombre", columnList = "nombre")
})
public class Departamento {
    
    @Id
    @Column(name = "codigo_dane", length = 2)
    @Size(min = 2, max = 2, message = "El código DANE del departamento debe tener 2 caracteres")
    @NotBlank(message = "El código DANE del departamento es obligatorio")
    private String codigoDane;
    
    @Column(name = "nombre", length = 100, nullable = false)
    @NotBlank(message = "El nombre del departamento es obligatorio")
    @Size(max = 100, message = "El nombre del departamento no puede exceder 100 caracteres")
    private String nombre;
    
    @Column(name = "capital", length = 100)
    @Size(max = 100, message = "El nombre de la capital no puede exceder 100 caracteres")
    private String capital;
    
    @Column(name = "region", length = 50)
    @Size(max = 50, message = "El nombre de la región no puede exceder 50 caracteres")
    private String region;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    // Relación uno a muchos con Municipios
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Municipio> municipios;
    
    // Constructores
    public Departamento() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }
    
    public Departamento(String codigoDane, String nombre) {
        this();
        this.codigoDane = codigoDane;
        this.nombre = nombre;
    }
    
    public Departamento(String codigoDane, String nombre, String capital, String region) {
        this(codigoDane, nombre);
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
    
    public List<Municipio> getMunicipios() {
        return municipios;
    }
    
    public void setMunicipios(List<Municipio> municipios) {
        this.municipios = municipios;
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
    
    @Override
    public String toString() {
        return "Departamento{" +
                "codigoDane='" + codigoDane + '\'' +
                ", nombre='" + nombre + '\'' +
                ", capital='" + capital + '\'' +
                ", region='" + region + '\'' +
                ", activo=" + activo +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Departamento that = (Departamento) o;
        return codigoDane != null ? codigoDane.equals(that.codigoDane) : that.codigoDane == null;
    }
    
    @Override
    public int hashCode() {
        return codigoDane != null ? codigoDane.hashCode() : 0;
    }
}

package com.claude.springboot.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa los tipos de documentos de identidad
 * 
 * @author Sistema PQRS
 * @version 1.0
 */
@Entity
@Table(name = "tipo_documento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 10, message = "El código no puede exceder 10 caracteres")
    @Column(name = "codigo", length = 10, nullable = false, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", length = 100, nullable = false, unique = true)
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Método que se ejecuta antes de persistir la entidad
     */
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = true;
        }
    }

    /**
     * Método que se ejecuta antes de actualizar la entidad
     */
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Constructor para crear un tipo de documento básico
     * 
     * @param codigo Código del tipo de documento
     * @param nombre Nombre del tipo de documento
     */
    public TipoDocumento(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.estado = true;
    }

    /**
     * Constructor completo sin fechas (se asignan automáticamente)
     * 
     * @param codigo      Código del tipo de documento
     * @param nombre      Nombre del tipo de documento
     * @param descripcion Descripción del tipo de documento
     * @param estado      Estado del tipo de documento
     */
    public TipoDocumento(String codigo, String nombre, String descripcion, Boolean estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado != null ? estado : true;
    }

    /**
     * Método para verificar si el tipo de documento está activo
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean isActivo() {
        return this.estado != null && this.estado;
    }

    /**
     * Método para activar el tipo de documento
     */
    public void activar() {
        this.estado = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Método para desactivar el tipo de documento
     */
    public void desactivar() {
        this.estado = false;
        this.fechaActualizacion = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TipoDocumento{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }
}

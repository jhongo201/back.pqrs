package com.claude.springboot.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de TipoDocumento
 * 
 * @author Sistema PQRS
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumentoDTO {

    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 10, message = "El código no puede exceder 10 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    /**
     * Constructor para crear un DTO básico
     * 
     * @param codigo Código del tipo de documento
     * @param nombre Nombre del tipo de documento
     */
    public TipoDocumentoDTO(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.estado = true;
    }

    /**
     * Constructor para crear un DTO con descripción
     * 
     * @param codigo      Código del tipo de documento
     * @param nombre      Nombre del tipo de documento
     * @param descripcion Descripción del tipo de documento
     */
    public TipoDocumentoDTO(String codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = true;
    }

    /**
     * Constructor completo sin fechas
     * 
     * @param codigo      Código del tipo de documento
     * @param nombre      Nombre del tipo de documento
     * @param descripcion Descripción del tipo de documento
     * @param estado      Estado del tipo de documento
     */
    public TipoDocumentoDTO(String codigo, String nombre, String descripcion, Boolean estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
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
     * Método para obtener el estado como texto
     * 
     * @return "Activo" o "Inactivo"
     */
    public String getEstadoTexto() {
        return isActivo() ? "Activo" : "Inactivo";
    }

    @Override
    public String toString() {
        return "TipoDocumentoDTO{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }
}

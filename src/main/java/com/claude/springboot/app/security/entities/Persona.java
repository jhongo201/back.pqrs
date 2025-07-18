package com.claude.springboot.app.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.claude.springboot.app.entities.Municipio;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personas")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_area")
    private Area area;
    
    // Relación con Municipio
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_municipio")
    private Municipio municipio;
    
    // Nuevos campos para nombres separados
    @Column(name = "primer_nombre", length = 100)
    private String primerNombre;
    
    @Column(name = "otros_nombres", length = 200)
    private String otrosNombres;
    
    @Column(name = "primer_apellido", length = 100)
    private String primerApellido;
    
    @Column(name = "segundo_apellido", length = 100)
    private String segundoApellido;
    
    // Campos legacy para compatibilidad
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;
    
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;
    
    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;
    
    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;
    
    @Column(name = "email", length = 100, unique = true)
    private String email;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "estado")
    private boolean estado;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    // Relación bidireccional con Usuario
    //@OneToOne(mappedBy = "persona", fetch = FetchType.LAZY)
    //@JsonBackReference
    @OneToOne(mappedBy = "persona")
    private Usuario usuario;
    
    // Métodos helper para obtener nombres completos
    @Transient
    public String getNombreCompleto() {
        // Usar los nuevos campos si están disponibles, sino usar los legacy
        if (primerNombre != null && primerApellido != null) {
            String nombreCompleto = primerNombre;
            if (otrosNombres != null && !otrosNombres.trim().isEmpty()) {
                nombreCompleto += " " + otrosNombres;
            }
            nombreCompleto += " " + primerApellido;
            if (segundoApellido != null && !segundoApellido.trim().isEmpty()) {
                nombreCompleto += " " + segundoApellido;
            }
            return nombreCompleto;
        }
        return nombres + " " + apellidos;
    }
    
    // Método para generar el campo nombres legacy
    @Transient
    public String generarNombresLegacy() {
        if (primerNombre != null) {
            String nombresCompletos = primerNombre;
            if (otrosNombres != null && !otrosNombres.trim().isEmpty()) {
                nombresCompletos += " " + otrosNombres;
            }
            return nombresCompletos;
        }
        return nombres;
    }
    
    // Método para generar el campo apellidos legacy
    @Transient
    public String generarApellidosLegacy() {
        if (primerApellido != null) {
            String apellidosCompletos = primerApellido;
            if (segundoApellido != null && !segundoApellido.trim().isEmpty()) {
                apellidosCompletos += " " + segundoApellido;
            }
            return apellidosCompletos;
        }
        return apellidos;
    }
    
    // Método helper para pre-persistencia
    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        estado = true;
        
        // Generar campos legacy automáticamente si los nuevos campos están presentes
        if (primerNombre != null && nombres == null) {
            nombres = generarNombresLegacy();
        }
        if (primerApellido != null && apellidos == null) {
            apellidos = generarApellidosLegacy();
        }
    }
    
    // Método para pre-actualización
    @PreUpdate
    public void preUpdate() {
        // Actualizar campos legacy si los nuevos campos han cambiado
        if (primerNombre != null) {
            nombres = generarNombresLegacy();
        }
        if (primerApellido != null) {
            apellidos = generarApellidosLegacy();
        }
    }
}

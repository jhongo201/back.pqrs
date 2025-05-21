package com.claude.springboot.app.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
    
    // Método helper para obtener nombre completo
    @Transient
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
    
    // Método helper para pre-persistencia
    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        estado = true;
    }
}

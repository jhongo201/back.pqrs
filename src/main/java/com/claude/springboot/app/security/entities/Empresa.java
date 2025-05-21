package com.claude.springboot.app.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empresas")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Long idEmpresa;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "nit", nullable = false, length = 20, unique = true)
    private String nit;
    
    @Column(name = "direccion")
    private String direccionFisica;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "estado")
    private boolean estado;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    // Relaciones
    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private Set<Territorial> territoriales;
    
    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private Set<Persona> personas;
    
    // Método helper para pre-persistencia
    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        estado = true;
    }

    
    
}
package com.claude.springboot.app.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

import com.claude.springboot.app.entities.TemasPqrs;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "areas")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_area")
    private Long idArea;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_direccion", nullable = false)
    private Direccion direccion;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "estado")
    private boolean estado;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
    private Set<Persona> personas;
    
    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
    private Set<TemasPqrs> temasPqrs;
    
    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        estado = true;
    }
    
    @Transient
    public String getRutaCompleta() {
        return direccion.getTerritorial().getEmpresa().getNombre() + " / " + 
               direccion.getTerritorial().getNombre() + " / " +
               direccion.getNombre() + " / " + 
               nombre;
    }
}
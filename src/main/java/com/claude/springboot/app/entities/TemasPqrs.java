package com.claude.springboot.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.claude.springboot.app.security.entities.Area;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "temas_pqrs")
public class TemasPqrs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tema")
    private Long idTema;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_area", nullable = false)
    private Area area;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "estado")
    private boolean estado;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        estado = true;
    }
}

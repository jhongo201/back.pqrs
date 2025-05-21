package com.claude.springboot.app.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rutas")
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Long idRuta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modulo")
    private Modulo modulo;
    
    @Column(name = "ruta")
    private String ruta;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "estado")
    private boolean estado;

    @Column(name = "es_publica")
    private boolean esPublica;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}

package com.claude.springboot.app.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString(exclude = "modulo")
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ruta ruta1 = (Ruta) o;
        return Objects.equals(idRuta, ruta1.idRuta) && 
               Objects.equals(ruta, ruta1.ruta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRuta, ruta);
    }
}

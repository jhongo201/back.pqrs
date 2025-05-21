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
@Table(name = "permisos_rol")
public class PermisoRol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private Rol rol;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;
    
    @Column(name = "puede_leer")
    private boolean puedeLeer;
    
    @Column(name = "puede_escribir")
    private boolean puedeEscribir;

    @Column(name = "puede_actualizar")
    private boolean puedeActualizar;
    
    @Column(name = "puede_eliminar")
    private boolean puedeEliminar;
    
    @Column(name = "estado")
    private boolean estado;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}

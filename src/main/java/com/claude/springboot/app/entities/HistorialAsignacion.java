package com.claude.springboot.app.entities;

import java.time.LocalDateTime;

import com.claude.springboot.app.security.entities.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historial_asignaciones")
@Data
@NoArgsConstructor
public class HistorialAsignacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pqrs")
    private Pqrs pqrs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_anterior")
    private Usuario usuarioAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_nuevo")
    private Usuario usuarioNuevo;

    @Column(name = "motivo_cambio")
    private String motivoCambio;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @PrePersist
    protected void onCreate() {
        fechaAsignacion = LocalDateTime.now();
    }
}

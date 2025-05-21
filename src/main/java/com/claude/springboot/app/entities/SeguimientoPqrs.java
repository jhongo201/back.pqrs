package com.claude.springboot.app.entities;

import java.time.LocalDateTime;

import com.claude.springboot.app.security.entities.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "seguimiento_pqrs")
@Data
@NoArgsConstructor
public class SeguimientoPqrs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Long idSeguimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pqrs")
    private Pqrs pqrs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String comentario;
    
    @Column(name = "archivo_adjunto")
    private String archivoAdjunto;
    
    @Column(name = "es_respuesta_final")
    private boolean esRespuestaFinal;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    @Column(name = "tipo_seguimiento")
    @Enumerated(EnumType.STRING)
    private TipoSeguimiento tipoSeguimiento = TipoSeguimiento.FUNCIONARIO; // valor por defecto

    public enum TipoSeguimiento {
        FUNCIONARIO,
        RESPUESTA_USUARIO,
        RESPUESTA_SOLICITANTE,
        ADJUNTO_INICIAL
    }


}

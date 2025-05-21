package com.claude.springboot.app.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.claude.springboot.app.security.entities.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pqrs")
@Data
@NoArgsConstructor
public class Pqrs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pqrs")
    private Long idPqrs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tema")
    private TemasPqrs tema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_asignado")
    private Usuario usuarioAsignado;

    @Column(name = "nombre_solicitante")
    private String nombreSolicitante;

    @Column(name = "email_solicitante")
    private String emailSolicitante;

    @Column(name = "telefono_solicitante")
    private String telefonoSolicitante;

    @Column(name = "tipo_documento_solicitante")
    private String tipoDocumentoSolicitante;

    @Column(name = "numero_documento_solicitante")
    private String numeroDocumentoSolicitante;

    private String titulo;
    private String descripcion;
    private String prioridad;
    
    @Column(name = "estado_pqrs")
    private String estadoPqrs;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;

    @Column(name = "numero_radicado", unique = true)
    private String numeroRadicado;

    @Column(name = "usuario_creador")
    private String usuarioCreador;  // Para guardar quién creó el PQRS

    @Column(name = "token_consulta", length = 100, unique = true)
    private String tokenConsulta;


    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaUltimaActualizacion = LocalDateTime.now();
        estadoPqrs = "PENDIENTE";
        tokenConsulta = UUID.randomUUID().toString();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaUltimaActualizacion = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "pqrs", fetch = FetchType.LAZY)
    private List<SeguimientoPqrs> seguimientos = new ArrayList<>();
}

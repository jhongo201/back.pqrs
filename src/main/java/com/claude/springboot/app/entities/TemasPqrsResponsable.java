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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "temas_pqrs_responsables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemasPqrsResponsable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tema")
    private TemasPqrs tema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @Column(name = "estado")
    private boolean estado;

    @PrePersist
    public void prePersist() {
        fechaAsignacion = LocalDateTime.now();
        estado = true;
    }
}

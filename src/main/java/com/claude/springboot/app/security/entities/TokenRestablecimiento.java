package com.claude.springboot.app.security.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_restablecimiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRestablecimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Long idToken;
    
    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;
    
    @Column(name = "codigo_restablecimiento", length = 6)
    private String codigoRestablecimiento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;
    
    @Column(name = "estado", nullable = false)
    private boolean estado = true;
    
    @Column(name = "usado", nullable = false)
    private boolean usado = false;
    
    @Column(name = "fecha_uso")
    private LocalDateTime fechaUso;
    
    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (fechaExpiracion == null) {
            fechaExpiracion = LocalDateTime.now().plusHours(1); // Token válido por 1 hora
        }
    }
}

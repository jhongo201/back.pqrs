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
@Table(name = "usuarios")
@NamedEntityGraph(
    name = "Usuario.completo",
    attributeNodes = {
        @NamedAttributeNode("rol"),
        @NamedAttributeNode("persona")
    }
)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")  // Cambiado de idUsuario a id_usuario
    private Long idUsuario;
    
    //@JsonManagedReference
    //@ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;
    
   // @JsonManagedReference
   //@ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;
    
    @Column(name = "username", unique = true)
    private String username;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "ultimo_login")  // Cambiado de ultimoLogin a ultimo_login
    private LocalDateTime ultimoLogin;
    
    @Column(name = "estado")
    private boolean estado;
    
    @Column(name = "fecha_creacion")  // Cambiado de fechaCreacion a fecha_creacion
    private LocalDateTime fechaCreacion;
    
    @Column(name = "token_sesion")  // Este ya está correcto
    private String tokenSesion;
    
    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        //estado = true;
    }

    public Object getNombreCompleto() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNombreCompleto'");
    }
}
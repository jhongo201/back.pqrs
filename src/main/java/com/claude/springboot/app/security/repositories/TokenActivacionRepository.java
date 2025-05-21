package com.claude.springboot.app.security.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claude.springboot.app.security.entities.TokenActivacion;
import com.claude.springboot.app.security.entities.Usuario;

@Repository
public interface TokenActivacionRepository extends JpaRepository<TokenActivacion, Long> {
    Optional<TokenActivacion> findByToken(String token);
    Optional<TokenActivacion> findByCodigoActivacion(String codigo);
    Optional<TokenActivacion> findByUsuarioAndEstadoTrue(Usuario usuario);
    // Obtiene todos los tokens activos de un usuario
    List<TokenActivacion> findAllByUsuarioAndEstadoTrue(Usuario usuario);
    Optional<TokenActivacion> findByUsuario(Usuario usuario);
    Optional<TokenActivacion> findFirstByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    // o
    Optional<TokenActivacion> findTopByUsuarioAndEstadoTrueOrderByFechaCreacionDesc(Usuario usuario);
}

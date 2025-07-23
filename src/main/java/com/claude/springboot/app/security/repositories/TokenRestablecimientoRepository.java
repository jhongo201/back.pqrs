package com.claude.springboot.app.security.repositories;

import com.claude.springboot.app.security.entities.TokenRestablecimiento;
import com.claude.springboot.app.security.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRestablecimientoRepository extends JpaRepository<TokenRestablecimiento, Long> {
    
    /**
     * Busca un token activo por su valor
     */
    Optional<TokenRestablecimiento> findByTokenAndEstadoTrueAndUsadoFalse(String token);
    
    /**
     * Busca un token activo por código de restablecimiento
     */
    Optional<TokenRestablecimiento> findByCodigoRestablecimientoAndEstadoTrueAndUsadoFalse(String codigoRestablecimiento);
    
    /**
     * Busca todos los tokens activos de un usuario
     */
    List<TokenRestablecimiento> findAllByUsuarioAndEstadoTrue(Usuario usuario);
    
    /**
     * Busca todos los tokens no usados de un usuario
     */
    List<TokenRestablecimiento> findAllByUsuarioAndEstadoTrueAndUsadoFalse(Usuario usuario);
    
    /**
     * Desactiva todos los tokens activos de un usuario
     */
    @Modifying
    @Query("UPDATE TokenRestablecimiento t SET t.estado = false WHERE t.usuario = :usuario AND t.estado = true")
    void desactivarTokensDelUsuario(@Param("usuario") Usuario usuario);
    
    /**
     * Elimina tokens expirados
     */
    @Modifying
    @Query("DELETE FROM TokenRestablecimiento t WHERE t.fechaExpiracion < :fechaActual")
    void eliminarTokensExpirados(@Param("fechaActual") LocalDateTime fechaActual);
    
    /**
     * Verifica si existe un token válido (no expirado, activo y no usado) para un usuario
     */
    @Query("SELECT COUNT(t) > 0 FROM TokenRestablecimiento t WHERE t.usuario = :usuario " +
           "AND t.estado = true AND t.usado = false AND t.fechaExpiracion > :fechaActual")
    boolean existeTokenValidoParaUsuario(@Param("usuario") Usuario usuario, @Param("fechaActual") LocalDateTime fechaActual);
}

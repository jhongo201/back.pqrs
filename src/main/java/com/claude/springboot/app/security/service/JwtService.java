package com.claude.springboot.app.security.service;


import com.claude.springboot.app.security.config.JwtConfig;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.UsuarioRepository;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtConfig jwtConfig;
    private final UsuarioRepository usuarioRepository;

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret().getBytes())
                .compact();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret().getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();

            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Asegúrate de que el rol tenga el prefijo ROLE_
            String roleName = "ROLE_" + usuario.getRol().getNombre();
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);

            UserDetails userDetails = User.builder()
                    .username(username)
                    .password("")
                    .authorities(Collections.singleton(authority))
                    .build();

            return new UsernamePasswordAuthenticationToken(userDetails, null, Collections.singleton(authority));
        } catch (RuntimeException e) {
            log.error("Error al obtener la autenticación", e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(jwtConfig.getSecret().getBytes())
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    public boolean isValidSessionToken(String username, String token) {
        try {
            log.debug("Validando token de sesión para usuario: {}", username);
            
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            log.debug("Token almacenado: {}", usuario.getTokenSesion());
            log.debug("Token recibido: {}", token);
            log.debug("Estado usuario: {}", usuario.isEstado());
            log.debug("Último login: {}", usuario.getUltimoLogin());
            
            if (usuario.getTokenSesion() == null) {
                log.warn("No hay token de sesión almacenado para el usuario");
                return false;
            }
            
            if (!token.equals(usuario.getTokenSesion())) {
                log.warn("El token no coincide con el almacenado");
                return false;
            }
            
            if (!usuario.isEstado()) {
                log.warn("Usuario no está activo");
                return false;
            }
            
            if (usuario.getUltimoLogin() == null) {
                log.warn("No hay registro de último login");
                return false;
            }

            LocalDateTime expirationTime = usuario.getUltimoLogin()
                    .plusSeconds(jwtConfig.getExpiration() / 1000);
            
            boolean isValid = LocalDateTime.now().isBefore(expirationTime);
            log.debug("Token válido: {}", isValid);
            
            return isValid;
        } catch (Exception e) {
            log.error("Error validando token de sesión", e);
            return false;
        }
    }

    public void updateUserSessionToken(String username, String token) {
        try {
            log.debug("Actualizando token de sesión para usuario: {}", username);
            
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usuario.setTokenSesion(token);
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);
            
            log.debug("Token de sesión actualizado exitosamente");
        } catch (Exception e) {
            log.error("Error actualizando el token de sesión", e);
            throw new RuntimeException("Error actualizando el token de sesión", e);
        }
    }

    /**
     * Invalida la sesión del usuario eliminando su token de sesión
     */
    public void invalidateUserSession(String username) {
        log.info("Invalidando sesión para el usuario: {}", username);
        try {
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
            
            usuario.setTokenSesion(null);
            usuario.setUltimoLogin(null);
            usuarioRepository.save(usuario);
            
            log.info("Sesión invalidada exitosamente para el usuario: {}", username);
        } catch (Exception e) {
            log.error("Error al invalidar la sesión del usuario: {}", username, e);
            throw new RuntimeException("Error al invalidar la sesión del usuario", e);
        }
    }

}
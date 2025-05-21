package com.claude.springboot.app.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claude.springboot.app.security.entities.TokenActivacion;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.TokenActivacionRepository;
import com.claude.springboot.app.security.repositories.UsuarioRepository;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final TokenActivacionRepository tokenActivacionRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.debug("Buscando usuario por username: {}", username);
            
            // Si el username no tiene @mintrabajo.loc, agregarlo
            String fullUsername = username.contains("@") ? username : username + "@mintrabajo.loc";

            // Primero buscar en la base de datos local
            Usuario usuario = usuarioRepository.findByUsername(fullUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + fullUsername));

            log.debug("Usuario encontrado: {}, Estado: {}", usuario.getUsername(), usuario.isEstado());

            // Verificar token de activación
            Optional<TokenActivacion> tokenOpt = tokenActivacionRepository
                    .findFirstByUsuarioOrderByFechaCreacionDesc(usuario);

            if (tokenOpt.isPresent()) {
                TokenActivacion token = tokenOpt.get();
                if (token.isEstado()) {
                    throw new DisabledException(
                            "CUENTA_NO_ACTIVADA:Por favor active su cuenta a través del enlace enviado a su correo electrónico");
                }
            }

            // Verificar estado del usuario
            if (!usuario.isEstado()) {
                throw new DisabledException("CUENTA_DESHABILITADA:Su cuenta está deshabilitada");
            }

            String roleName = usuario.getRol().getNombre();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }

            return User.builder()
                    .username(usuario.getUsername())
                    .password(usuario.getPassword())
                    .authorities(Collections.singleton(new SimpleGrantedAuthority(roleName)))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(!usuario.isEstado())
                    .build();

        } catch (DisabledException e) {
            log.error("Error de cuenta deshabilitada: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error al cargar usuario: {}", e.getMessage());
            throw new UsernameNotFoundException("Error al cargar usuario: " + e.getMessage());
        }
    }

    
}

package com.claude.springboot.app.security.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.claude.springboot.app.security.entities.Rol;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.RolRepository;
import com.claude.springboot.app.security.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final LdapService ldapService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        log.debug("Intentando autenticar usuario: {}", username);
        
        try {
            // Primero intentar autenticación local
            Optional<Usuario> usuarioLocal = usuarioRepository.findByUsername(username);
            if (usuarioLocal.isPresent()) {
                Usuario usuario = usuarioLocal.get();
                if (passwordEncoder.matches(password, usuario.getPassword())) {
                    log.info("Autenticación local exitosa para: {}", username);
                    return createAuthenticationToken(usuario);
                }
            }

            // Si la autenticación local falla, intentar LDAP
            String fullUsername = username + "@mintrabajo.loc";
            if (ldapService.authenticate(fullUsername, password)) {
                log.info("Autenticación LDAP exitosa para: {}", fullUsername);
                
                // Buscar o crear usuario local
                Usuario usuario = usuarioRepository.findByUsername(fullUsername)
                    .orElseGet(() -> createLocalUserFromLdap(fullUsername, password));

                return createAuthenticationToken(usuario);
            }
        } catch (Exception e) {
            log.error("Error en proceso de autenticación: {}", e.getMessage());
        }
        
        throw new BadCredentialsException("Credenciales inválidas");
    }

    private Authentication createAuthenticationToken(Usuario usuario) {
        UserDetails userDetails = User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword() != null ? usuario.getPassword() : "")
            .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!usuario.isEstado())
            .build();

        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }

    private Usuario createLocalUserFromLdap(String username, String password) {
        log.debug("Creando usuario local desde LDAP: {}", username);
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEstado(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setPassword(passwordEncoder.encode(password));
        
        Rol rolDefault = rolRepository.findByNombre("USUARIO")
            .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
        usuario.setRol(rolDefault);
        
        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
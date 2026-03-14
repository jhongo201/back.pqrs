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

import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.UsuarioRepository;

import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final LdapService ldapService;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        log.info("=== INICIO AUTENTICACIÓN ===");
        log.info("Intentando autenticar usuario: [{}]", username);
        
        try {
            // Normalizar username para consistencia
            String normalizedUsername = normalizeUsername(username);
            log.info("Username normalizado: [{}]", normalizedUsername);
            
            // Buscar usuario existente con múltiples formatos
            Optional<Usuario> usuarioExistente = findExistingUser(username, normalizedUsername);
            log.info("Usuario encontrado en BD: {}", usuarioExistente.isPresent());
            
            if (usuarioExistente.isPresent()) {
                Usuario usuario = usuarioExistente.get();
                log.info("Usuario encontrado - ID: {}, Username: [{}], Tiene password: {}", 
                        usuario.getIdUsuario(), usuario.getUsername(), 
                        usuario.getPassword() != null && !usuario.getPassword().isEmpty());
                
                // Si tiene contraseña local, intentar autenticación local
                if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                    if (passwordEncoder.matches(password, usuario.getPassword())) {
                        log.info("Autenticación local exitosa para: {}", usuario.getUsername());
                        return createAuthenticationToken(usuario);
                    }
                    log.info("Contraseña local no coincide, intentando LDAP...");
                }
                
                // Si es usuario LDAP o falla auth local, intentar LDAP
                log.info("Intentando autenticación LDAP para username: [{}]", normalizedUsername);
                boolean ldapAuth = ldapService.authenticate(normalizedUsername, password);
                log.info("Resultado autenticación LDAP: {}", ldapAuth);
                
                if (ldapAuth) {
                    log.info("Autenticación LDAP exitosa para usuario existente: {}", normalizedUsername);
                    
                    // Actualizar username si es necesario para consistencia
                    if (!usuario.getUsername().equals(normalizedUsername)) {
                        log.info("Actualizando username de {} a {}", usuario.getUsername(), normalizedUsername);
                        Optional<Usuario> usuarioConUsernameNormalizado = usuarioRepository.findByUsername(normalizedUsername);
                        if (usuarioConUsernameNormalizado.isPresent()
                                && !usuarioConUsernameNormalizado.get().getIdUsuario().equals(usuario.getIdUsuario())) {
                            log.warn("Ya existe otro usuario con username normalizado: [{}] (ID: {}). Se usará ese registro para autenticar.",
                                    normalizedUsername, usuarioConUsernameNormalizado.get().getIdUsuario());
                            usuario = usuarioConUsernameNormalizado.get();
                        } else {
                            try {
                                usuario.setUsername(normalizedUsername);
                                usuario = usuarioRepository.save(usuario);
                            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                                log.warn("No fue posible actualizar username a [{}] por restricción de integridad. Se continuará autenticación sin actualizar. Detalle: {}",
                                        normalizedUsername, e.getMessage());
                            }
                        }
                    }
                    
                    return createAuthenticationToken(usuario);
                } else {
                    log.warn("Autenticación LDAP falló para usuario existente: {}", normalizedUsername);
                }
            } else {
                log.warn("Usuario no encontrado en BD: [{}]. No se permite autocreación para usuarios LDAP.", normalizedUsername);
                throw new BadCredentialsException("Usuario no registrado");
            }
        } catch (Exception e) {
            log.error("Error en proceso de autenticación para usuario [{}]: {}", username, e.getMessage(), e);
        }
        
        log.info("=== FIN AUTENTICACIÓN - CREDENCIALES INVÁLIDAS ===");
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

    /**
     * Normaliza el username para asegurar formato consistente
     */
    private String normalizeUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username no puede ser nulo o vacío");
        }
        
        // Si ya tiene dominio, devolverlo tal como está
        if (username.contains("@")) {
            return username.toLowerCase().trim();
        }
        
        // Si no tiene dominio, agregarlo
        return (username.toLowerCase().trim() + "@mintrabajo.loc");
    }
    
    /**
     * Busca usuario existente probando diferentes formatos de username
     */
    private Optional<Usuario> findExistingUser(String originalUsername, String normalizedUsername) {
        log.info("Buscando usuario con formatos: original=[{}], normalizado=[{}]", originalUsername, normalizedUsername);
        
        // Buscar por username original
        Optional<Usuario> usuario = usuarioRepository.findByUsername(originalUsername);
        if (usuario.isPresent()) {
            log.info("Usuario encontrado con username original: [{}] - ID: {}", originalUsername, usuario.get().getIdUsuario());
            return usuario;
        }
        log.info("No encontrado con username original: [{}]", originalUsername);
        
        // Buscar por username normalizado
        usuario = usuarioRepository.findByUsername(normalizedUsername);
        if (usuario.isPresent()) {
            log.info("Usuario encontrado con username normalizado: [{}] - ID: {}", normalizedUsername, usuario.get().getIdUsuario());
            return usuario;
        }
        log.info("No encontrado con username normalizado: [{}]", normalizedUsername);
        
        // Buscar por username sin dominio (si el normalizado lo tiene)
        if (normalizedUsername.contains("@")) {
            String usernameWithoutDomain = normalizedUsername.split("@")[0];
            log.info("Probando con username sin dominio: [{}]", usernameWithoutDomain);
            usuario = usuarioRepository.findByUsername(usernameWithoutDomain);
            if (usuario.isPresent()) {
                log.info("Usuario encontrado con username sin dominio: [{}] - ID: {}", usernameWithoutDomain, usuario.get().getIdUsuario());
                return usuario;
            }
            log.info("No encontrado con username sin dominio: [{}]", usernameWithoutDomain);
        }
        
        log.info("NO SE ENCONTRÓ USUARIO con ninguno de los formatos probados");
        return Optional.empty();
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
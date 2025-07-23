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
            // Normalizar username para consistencia
            String normalizedUsername = normalizeUsername(username);
            log.debug("Username normalizado: {}", normalizedUsername);
            
            // Buscar usuario existente con múltiples formatos
            Optional<Usuario> usuarioExistente = findExistingUser(username, normalizedUsername);
            
            if (usuarioExistente.isPresent()) {
                Usuario usuario = usuarioExistente.get();
                
                // Si tiene contraseña local, intentar autenticación local
                if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                    if (passwordEncoder.matches(password, usuario.getPassword())) {
                        log.info("Autenticación local exitosa para: {}", usuario.getUsername());
                        return createAuthenticationToken(usuario);
                    }
                }
                
                // Si es usuario LDAP o falla auth local, intentar LDAP
                if (ldapService.authenticate(normalizedUsername, password)) {
                    log.info("Autenticación LDAP exitosa para usuario existente: {}", normalizedUsername);
                    
                    // Actualizar username si es necesario para consistencia
                    if (!usuario.getUsername().equals(normalizedUsername)) {
                        log.info("Actualizando username de {} a {}", usuario.getUsername(), normalizedUsername);
                        usuario.setUsername(normalizedUsername);
                        usuario = usuarioRepository.save(usuario);
                    }
                    
                    return createAuthenticationToken(usuario);
                }
            } else {
                // Usuario no existe, intentar LDAP y crear si es exitoso
                if (ldapService.authenticate(normalizedUsername, password)) {
                    log.info("Autenticación LDAP exitosa para nuevo usuario: {}", normalizedUsername);
                    
                    // Crear usuario local
                    Usuario nuevoUsuario = createLocalUserFromLdap(normalizedUsername);
                    return createAuthenticationToken(nuevoUsuario);
                }
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
        // Buscar por username original
        Optional<Usuario> usuario = usuarioRepository.findByUsername(originalUsername);
        if (usuario.isPresent()) {
            log.debug("Usuario encontrado con username original: {}", originalUsername);
            return usuario;
        }
        
        // Buscar por username normalizado
        usuario = usuarioRepository.findByUsername(normalizedUsername);
        if (usuario.isPresent()) {
            log.debug("Usuario encontrado con username normalizado: {}", normalizedUsername);
            return usuario;
        }
        
        // Buscar por username sin dominio (si el normalizado lo tiene)
        if (normalizedUsername.contains("@")) {
            String usernameWithoutDomain = normalizedUsername.split("@")[0];
            usuario = usuarioRepository.findByUsername(usernameWithoutDomain);
            if (usuario.isPresent()) {
                log.debug("Usuario encontrado con username sin dominio: {}", usernameWithoutDomain);
                return usuario;
            }
        }
        
        log.debug("No se encontró usuario existente para: {} / {}", originalUsername, normalizedUsername);
        return Optional.empty();
    }
    
    /**
     * Crea usuario local desde LDAP sin almacenar contraseña
     */
    private Usuario createLocalUserFromLdap(String normalizedUsername) {
        log.info("Creando nuevo usuario local desde LDAP: {}", normalizedUsername);
        
        Usuario usuario = new Usuario();
        usuario.setUsername(normalizedUsername);
        usuario.setEstado(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setPassword(""); // Usuario LDAP, cadena vacía (BD no permite null)
        
        Rol rolDefault = rolRepository.findByNombre("USUARIO")
            .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));
        usuario.setRol(rolDefault);
        
        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
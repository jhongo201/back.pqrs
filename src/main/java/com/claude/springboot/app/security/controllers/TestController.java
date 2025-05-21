package com.claude.springboot.app.security.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.claude.springboot.app.security.config.SecurityConfig;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    private final SecurityConfig securityConfig;
    
    
    public TestController(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
        // Imprimir la contraseña encriptada al iniciar
        System.out.println("Contraseña encriptada para 'password': " + 
            securityConfig.generateEncodedPassword("password"));
    }

    @GetMapping("/password/{rawPassword}")
    public ResponseEntity<Map<String, String>> getEncodedPassword(@PathVariable String rawPassword) {
        String encodedPassword = securityConfig.generateEncodedPassword(rawPassword);
        Map<String, String> response = new HashMap<>();
        response.put("rawPassword", rawPassword);
        response.put("encodedPassword", encodedPassword);
        System.out.println("Contraseña encriptada para 'password': " +  encodedPassword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Este es un endpoint público");
    }

    @GetMapping("/private")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> privateEndpoint() {
        return ResponseEntity.ok("Este es un endpoint privado");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Este es un endpoint solo para administradores");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Map<String, Object> response = new HashMap<>();
    
    // Información básica de autenticación
    response.put("username", auth.getName());
    response.put("authorities", auth.getAuthorities());
    response.put("isAuthenticated", auth.isAuthenticated());
    
    // Si el usuario está autenticado, obtener más información
    if (auth.getPrincipal() instanceof UserDetails) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        response.put("accountNonExpired", userDetails.isAccountNonExpired());
        response.put("accountNonLocked", userDetails.isAccountNonLocked());
        response.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
        response.put("enabled", userDetails.isEnabled());
    }
    
    // Información del token (si está disponible en el request)
    String authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest().getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        response.put("currentToken", authHeader.substring(7));
    }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ldap")
    public ResponseEntity<Map<String, Object>> testLdapConnection(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Autenticación LDAP exitosa");
        response.put("username", userDetails.getUsername());
        response.put("authorities", userDetails.getAuthorities());
        return ResponseEntity.ok(response);
    }
    
}

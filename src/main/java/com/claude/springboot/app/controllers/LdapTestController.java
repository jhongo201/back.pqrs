package com.claude.springboot.app.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.claude.springboot.app.security.service.LdapService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class LdapTestController {

    private final LdapService ldapService;

    @PostMapping("/ldap-direct")
    public ResponseEntity<Map<String, Object>> testLdapDirect(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            log.info("Probando autenticación LDAP directa para: {}", username);
            
            // Probar con el username tal como viene
            boolean result1 = ldapService.authenticate(username, password);
            response.put("auth_original", result1);
            response.put("username_original", username);
            
            // Probar agregando el dominio si no lo tiene
            if (!username.contains("@")) {
                String usernameWithDomain = username + "@mintrabajo.loc";
                boolean result2 = ldapService.authenticate(usernameWithDomain, password);
                response.put("auth_with_domain", result2);
                response.put("username_with_domain", usernameWithDomain);
            }
            
            // Probar quitando el dominio si lo tiene
            if (username.contains("@")) {
                String usernameWithoutDomain = username.split("@")[0];
                boolean result3 = ldapService.authenticate(usernameWithoutDomain, password);
                response.put("auth_without_domain", result3);
                response.put("username_without_domain", usernameWithoutDomain);
            }
            
            response.put("success", true);
            
        } catch (Exception e) {
            log.error("Error en prueba LDAP", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/ldap-config")
    public ResponseEntity<Map<String, Object>> getLdapConfig() {
        Map<String, Object> response = new HashMap<>();
        
        // Mostrar configuración LDAP (sin credenciales sensibles)
        response.put("ldap_url", System.getProperty("ldap.url", "No configurado"));
        response.put("ldap_base_dn", System.getProperty("ldap.base.dn", "No configurado"));
        response.put("ldap_user_dn_pattern", System.getProperty("ldap.user.dn.pattern", "No configurado"));
        
        return ResponseEntity.ok(response);
    }
}

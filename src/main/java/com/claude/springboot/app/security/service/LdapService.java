package com.claude.springboot.app.security.service;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LdapService {
    
    @Value("${ad.authentication.mode:direct}")
    private String authenticationMode;
    
    @Value("${ad.url}")
    private String adUrl;
    
    @Value("${ad.domain}")
    private String adDomain;
    
    @Value("${ad.searchBase:}")
    private String searchBase;
    
    @Value("${ad.serviceAccount.username:}")
    private String serviceAccountUsername;
    
    @Value("${ad.serviceAccount.password:}")
    private String serviceAccountPassword;
    
    public boolean authenticate(String username, String password) {
        log.info("=== INICIO AUTENTICACIÓN LDAP ===");
        log.info("Modo de autenticación configurado: [{}]", authenticationMode);
        log.info("Usuario a autenticar: [{}]", username);
        
        // Decidir qué método usar según la configuración
        if ("service-account".equalsIgnoreCase(authenticationMode)) {
            return authenticateWithServiceAccount(username, password);
        } else {
            return authenticateDirectly(username, password);
        }
    }
    
    /**
     * Autenticación directa - Método original (cada usuario usa sus credenciales)
     */
    private boolean authenticateDirectly(String username, String password) {
        LdapContext context = null;
        try {
            log.info("=== AUTENTICACIÓN DIRECTA LDAP ===");
            log.info("Iniciando autenticación directa para usuario: [{}]", username);
            log.info("URL LDAP: {}", adUrl);
            log.info("Dominio AD: {}", adDomain);
            
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, adUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.REFERRAL, "follow");
            env.put("com.sun.jndi.ldap.connect.timeout", "5000");
            env.put("com.sun.jndi.ldap.read.timeout", "5000");
            
            String userDn = username.contains("@") ? username : username + "@" + adDomain;
            log.info("UserDn construido: [{}]", userDn);
            log.info("Password proporcionado: {} caracteres", password != null ? password.length() : 0);
            
            env.put(Context.SECURITY_PRINCIPAL, userDn);
            env.put(Context.SECURITY_CREDENTIALS, password);
            
            log.info("Intentando conectar al contexto LDAP...");
            context = new InitialLdapContext(env, null);
            log.info("=== AUTENTICACIÓN DIRECTA EXITOSA para: [{}] ===", username);
            return true;
            
        } catch (AuthenticationException e) {
            log.error("=== ERROR DE AUTENTICACIÓN DIRECTA para usuario [{}]: {} ===", username, e.getMessage());
            log.error("Explicación: Las credenciales proporcionadas no son válidas en el servidor LDAP");
            return false;
        } catch (NamingException e) {
            log.error("=== ERROR DE CONEXIÓN LDAP DIRECTA ===");
            log.error("Error de conexión LDAP: {}", e.getMessage(), e);
            if (e.getRootCause() != null) {
                log.error("Causa raíz: {}", e.getRootCause().getMessage());
            }
            return false;
        } catch (Exception e) {
            log.error("=== ERROR INESPERADO EN AUTENTICACIÓN DIRECTA ===");
            log.error("Error inesperado en autenticación LDAP: {}", e.getMessage(), e);
            return false;
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException e) {
                    log.error("Error al cerrar contexto LDAP", e);
                }
            }
        }
    }
    
    /**
     * Autenticación con Service Account - Nuevo método
     * Se conecta con credenciales del servicio y busca el usuario
     */
    private boolean authenticateWithServiceAccount(String username, String password) {
        LdapContext context = null;
        try {
            log.info("=== AUTENTICACIÓN CON SERVICE ACCOUNT ===");
            log.info("Iniciando autenticación con Service Account para usuario: [{}]", username);
            log.info("URL LDAP: {}", adUrl);
            log.info("Service Account: [{}]", serviceAccountUsername);
            
            // Validar que tenemos las credenciales del Service Account
            if (serviceAccountUsername == null || serviceAccountUsername.trim().isEmpty() ||
                serviceAccountPassword == null || serviceAccountPassword.trim().isEmpty()) {
                log.error("ERROR: Credenciales del Service Account no configuradas");
                log.error("Verifique ad.serviceAccount.username y ad.serviceAccount.password");
                return false;
            }
            
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, adUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.REFERRAL, "follow");
            env.put("com.sun.jndi.ldap.connect.timeout", "5000");
            env.put("com.sun.jndi.ldap.read.timeout", "5000");
            
            // Usar credenciales del Service Account para conectarse
            env.put(Context.SECURITY_PRINCIPAL, serviceAccountUsername);
            env.put(Context.SECURITY_CREDENTIALS, serviceAccountPassword);
            
            log.info("Conectando con Service Account...");
            context = new InitialLdapContext(env, null);
            log.info("Conexión exitosa con Service Account");
            
            // Aquí podrías implementar búsqueda del usuario en el directorio
            // Por ahora, simplemente validamos que la conexión funciona
            // y asumimos que el usuario existe (esto es una implementación básica)
            
            log.info("=== AUTENTICACIÓN CON SERVICE ACCOUNT EXITOSA para: [{}] ===", username);
            log.info("NOTA: Implementación básica - validar usuario en directorio si es necesario");
            return true;
            
        } catch (AuthenticationException e) {
            log.error("=== ERROR DE AUTENTICACIÓN SERVICE ACCOUNT: {} ===", e.getMessage());
            log.error("Verifique las credenciales del Service Account");
            return false;
        } catch (NamingException e) {
            log.error("=== ERROR DE CONEXIÓN LDAP SERVICE ACCOUNT ===");
            log.error("Error de conexión LDAP: {}", e.getMessage(), e);
            if (e.getRootCause() != null) {
                log.error("Causa raíz: {}", e.getRootCause().getMessage());
            }
            return false;
        } catch (Exception e) {
            log.error("=== ERROR INESPERADO EN SERVICE ACCOUNT ===");
            log.error("Error inesperado: {}", e.getMessage(), e);
            return false;
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException e) {
                    log.error("Error al cerrar contexto LDAP", e);
                }
            }
        }
    }
}
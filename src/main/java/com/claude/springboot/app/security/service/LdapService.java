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
    
    @Value("${ad.url}")
    private String adUrl;
    
    @Value("${ad.domain}")
    private String adDomain;
    
    public boolean authenticate(String username, String password) {
        LdapContext context = null;
        try {
            log.info("=== INICIO AUTENTICACIÓN LDAP ===");
            log.info("Iniciando autenticación LDAP para usuario: [{}]", username);
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
            log.info("=== AUTENTICACIÓN LDAP EXITOSA para: [{}] ===", username);
            return true;
            
        } catch (AuthenticationException e) {
            log.error("=== ERROR DE AUTENTICACIÓN LDAP para usuario [{}]: {} ===", username, e.getMessage());
            log.error("Explicación: Las credenciales proporcionadas no son válidas en el servidor LDAP");
            return false;
        } catch (NamingException e) {
            log.error("=== ERROR DE CONEXIÓN LDAP ===");
            log.error("Error de conexión LDAP: {}", e.getMessage(), e);
            if (e.getRootCause() != null) {
                log.error("Causa raíz: {}", e.getRootCause().getMessage());
            }
            return false;
        } catch (Exception e) {
            log.error("=== ERROR INESPERADO EN AUTENTICACIÓN LDAP ===");
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
}
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
            log.debug("Iniciando autenticación LDAP para usuario: {}", username);
            log.debug("URL LDAP: {}", adUrl);
            
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, adUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.REFERRAL, "follow");
            env.put("com.sun.jndi.ldap.connect.timeout", "5000");
            env.put("com.sun.jndi.ldap.read.timeout", "5000");
            
            String userDn = username.contains("@") ? username : username + "@" + adDomain;
            log.debug("Intentando autenticar con userDn: {}", userDn);
            
            env.put(Context.SECURITY_PRINCIPAL, userDn);
            env.put(Context.SECURITY_CREDENTIALS, password);
            
            context = new InitialLdapContext(env, null);
            log.info("Autenticación LDAP exitosa para: {}", username);
            return true;
            
        } catch (AuthenticationException e) {
            log.error("Error de autenticación LDAP para usuario {}: {}", username, e.getMessage());
            return false;
        } catch (NamingException e) {
            log.error("Error de conexión LDAP: {}", e.getMessage(), e);
            log.error("Causa raíz: ", e.getRootCause());
            return false;
        } catch (Exception e) {
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
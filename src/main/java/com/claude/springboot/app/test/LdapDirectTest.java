package com.claude.springboot.app.test;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.AuthenticationException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

public class LdapDirectTest {
    
    public static void main(String[] args) {
        String adUrl = "ldap://mintrabajo.loc:389";
        String adDomain = "mintrabajo.loc";
        String username = "jperezc";
        String password = "Bogota2024+5";
        
        System.out.println("=== PRUEBA DIRECTA DE AUTENTICACIÓN LDAP ===");
        System.out.println("URL LDAP: " + adUrl);
        System.out.println("Dominio: " + adDomain);
        System.out.println("Usuario: " + username);
        System.out.println();
        
        // Usar exactamente la misma lógica que LdapService
        boolean result = authenticateExactlyLikeLdapService(adUrl, adDomain, username, password);
        System.out.println("Resultado final: " + (result ? "ÉXITO" : "FALLO"));
    }
    
    // Usar exactamente la misma lógica que LdapService.authenticate()
    private static boolean authenticateExactlyLikeLdapService(String adUrl, String adDomain, String username, String password) {
        LdapContext context = null;
        try {
            System.out.println("Iniciando autenticación LDAP para usuario: " + username);
            System.out.println("URL LDAP: " + adUrl);
            
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, adUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.REFERRAL, "follow");
            env.put("com.sun.jndi.ldap.connect.timeout", "5000");
            env.put("com.sun.jndi.ldap.read.timeout", "5000");
            
            String userDn = username.contains("@") ? username : username + "@" + adDomain;
            System.out.println("Intentando autenticar con userDn: " + userDn);
            
            env.put(Context.SECURITY_PRINCIPAL, userDn);
            env.put(Context.SECURITY_CREDENTIALS, password);
            
            context = new InitialLdapContext(env, null);
            System.out.println("Autenticación LDAP exitosa para: " + username);
            return true;
            
        } catch (AuthenticationException e) {
            System.out.println("Error de autenticación LDAP para usuario " + username + ": " + e.getMessage());
            return false;
        } catch (NamingException e) {
            System.out.println("Error de conexión LDAP: " + e.getMessage());
            if (e.getRootCause() != null) {
                System.out.println("Causa raíz: " + e.getRootCause().getMessage());
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error inesperado en autenticación LDAP: " + e.getMessage());
            return false;
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException e) {
                    System.out.println("Error al cerrar contexto LDAP: " + e.getMessage());
                }
            }
        }
    }
}

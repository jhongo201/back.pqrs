package com.claude.springboot.app.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para generar contraseñas encriptadas con BCrypt
 * Solo para uso en desarrollo y testing
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generar contraseñas para usuarios de prueba
        String adminPassword = "admin123";
        String funcionarioPassword = "func123";
        
        String adminEncrypted = encoder.encode(adminPassword);
        String funcionarioEncrypted = encoder.encode(funcionarioPassword);
        
        System.out.println("=== CONTRASEÑAS ENCRIPTADAS ===");
        System.out.println("admin123 -> " + adminEncrypted);
        System.out.println("func123 -> " + funcionarioEncrypted);
        
        // Verificar que las contraseñas funcionen
        System.out.println("\n=== VERIFICACIÓN ===");
        System.out.println("admin123 matches: " + encoder.matches(adminPassword, adminEncrypted));
        System.out.println("func123 matches: " + encoder.matches(funcionarioPassword, funcionarioEncrypted));
    }
}

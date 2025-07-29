package com.claude.springboot.app;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Clase para inicialización en servidores de aplicaciones externos (Tomcat, etc.)
 * Necesaria para despliegue WAR en servidores externos
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // Usar referencia completa de la clase para evitar problemas de IDE
        return application.sources(com.claude.springboot.app.SpringbootClaudeApplication.class);
    }
}

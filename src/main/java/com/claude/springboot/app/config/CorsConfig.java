package com.claude.springboot.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciales
        config.setAllowCredentials(true);
        
        // Orígenes permitidos
        config.addAllowedOrigin("http://localhost:4200"); // Angular por defecto
        config.addAllowedOrigin("http://mst.mintrabajo.gov.co"); // Frontend de producción
        config.addAllowedOrigin("https://mst.mintrabajo.gov.co"); // Frontend de producción
        
        // IPs del servidor para acceso directo
        config.addAllowedOrigin("http://10.1.0.20:8080"); // IP privada
        config.addAllowedOrigin("http://20.110.161.158:8080"); // IP pública
        config.addAllowedOrigin("https://20.110.161.158:8080"); // IP pública HTTPS
        config.addAllowedOrigin("http://mst.mintrabajo.gov.co:8080"); // Dominio HTTP
        config.addAllowedOrigin("https://mst.mintrabajo.gov.co:8080"); // Dominio HTTPS
        
        // Agregar otros orígenes según necesites
        // config.addAllowedOrigin("https://tu-dominio-produccion.com");
        
        // Métodos HTTP permitidos
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        
        // Headers permitidos
        config.addAllowedHeader("Origin");
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Access-Control-Allow-Origin");
        config.addAllowedHeader("Access-Control-Allow-Credentials");
        
        // Exponer headers
        config.addExposedHeader("Authorization");
        
        // Tiempo máximo de cache de pre-flight
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

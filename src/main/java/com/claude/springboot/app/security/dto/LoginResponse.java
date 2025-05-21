package com.claude.springboot.app.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String message;
    private String token;
    private String username;
    private String rol;
    private String nombreCompleto;
    
    @Data
    @Builder
    public static class AreaInfo {
        private Long id;
        private String nombre;
    }
    
    @Data
    @Builder
    public static class DireccionInfo {
        private Long id;
        private String nombre;
    }
    
    @Data
    @Builder
    public static class EmpresaInfo {
        private Long id;
        private String nombre;
    }
    
    private AreaInfo area;
    private DireccionInfo direccion;
    private EmpresaInfo empresa;
}

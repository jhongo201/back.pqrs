package com.claude.springboot.app.security.service;

import com.claude.springboot.app.security.dto.MenuResponseDTO;

public interface MenuService {
    /**
     * Obtiene las opciones de menú para un usuario específico basado en sus permisos
     * 
     * @param username Nombre de usuario
     * @return DTO con la estructura jerárquica de módulos y rutas permitidas
     */
    MenuResponseDTO getMenuOptionsForUser(String username);
}

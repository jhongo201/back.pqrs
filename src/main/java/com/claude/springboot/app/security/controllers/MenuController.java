package com.claude.springboot.app.security.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claude.springboot.app.security.dto.MenuResponseDTO;
import com.claude.springboot.app.security.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuService menuService;
    
    @GetMapping("/opciones")
    public ResponseEntity<MenuResponseDTO> getOpcionesMenu() {
        // Obtener el usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Obtener las opciones de menú para el usuario
        MenuResponseDTO menuOptions = menuService.getMenuOptionsForUser(username);
        
        return ResponseEntity.ok(menuOptions);
    }
}

package com.claude.springboot.app.security.exception;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(Long id) {
        super("Usuario no encontrado con ID: " + id);
    }
}

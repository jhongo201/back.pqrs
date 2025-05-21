package com.claude.springboot.app.security.exception;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String username) {
        super("El nombre de usuario '" + username + "' ya existe");
    }
}

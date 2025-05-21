package com.claude.springboot.app.security.enums;

public enum TipoPermiso {
    LECTURA,
    ESCRITURA,
    ACTUALIZAR,
    ELIMINAR;

    public String getValue() {
        return this.name().toLowerCase();
    }
}

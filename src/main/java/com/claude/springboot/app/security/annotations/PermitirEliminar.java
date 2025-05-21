package com.claude.springboot.app.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@PreAuthorize("@permisoEvaluator.tienePermiso('/api/usuarios', 'ELIMINAR')")
//@PreAuthorize("@permisoService.tienePermiso(authentication.principal.username, #root.target.class.simpleName, T(com.claude.springboot.app.security.enums.TipoPermiso).ELIMINAR)")
@PreAuthorize("@permisoEvaluator.tienePermiso('/api/' + T(String).valueOf(#root.this.getClass().getSimpleName()).replace('Controller', '').toLowerCase() + 's', T(com.claude.springboot.app.security.enums.TipoPermiso).ELIMINAR)")
public @interface PermitirEliminar {
}

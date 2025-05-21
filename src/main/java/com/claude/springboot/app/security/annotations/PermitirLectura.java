package com.claude.springboot.app.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

//@PreAuthorize("@permisoService.tienePermiso(authentication.principal.username, #root.target.class.simpleName, T(com.claude.springboot.app.security.enums.TipoPermiso).LECTURA)")

//@PreAuthorize("@permisoEvaluator.tienePermiso('/api/usuarios', 'LECTURA')")
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//@PreAuthorize("@permisoService.tienePermiso(authentication, '/api/usuarios', T(com.claude.springboot.app.security.enums.TipoPermiso).LECTURA)")
//@PreAuthorize("@permisoEvaluator.tienePermiso(T(String).valueOf(#root.this.getClass().getSimpleName()).replace('Controller', '').toLowerCase(), 'LECTURA')")
@PreAuthorize("@permisoEvaluator.tienePermiso('/api/' + T(String).valueOf(#root.this.getClass().getSimpleName()).replace('Controller', '').toLowerCase() + 's', T(com.claude.springboot.app.security.enums.TipoPermiso).LECTURA)")
public @interface PermitirLectura {
}

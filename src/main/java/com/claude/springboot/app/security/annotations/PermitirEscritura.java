package com.claude.springboot.app.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@permisoEvaluator.tienePermiso('/api/' + T(String).valueOf(#root.this.getClass().getSimpleName()).replace('Controller', '').toLowerCase() + 's', T(com.claude.springboot.app.security.enums.TipoPermiso).ESCRITURA)")
public @interface PermitirEscritura {
}

package com.claude.springboot.app.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private long expiration;
    private String prefix;
    private String header;

    // Esta clave se usa si no se proporciona una en las propiedades
    public String getSecret() {
        if (secret == null || secret.trim().isEmpty()) {
            return "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        }
        return secret;
    }
}

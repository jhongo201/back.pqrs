package com.claude.springboot.app.security.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuditoriaInterceptor implements HandlerInterceptor {
    
    private final Logger log = LoggerFactory.getLogger(AuditoriaInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            log.info("Usuario {} accediendo a {} mediante {}",
                auth.getName(),
                request.getRequestURI(),
                request.getMethod()
            );
        }
        return true;
    }
}

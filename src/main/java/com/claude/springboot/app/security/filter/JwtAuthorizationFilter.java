package com.claude.springboot.app.security.filter;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.claude.springboot.app.security.config.JwtConfig;
import com.claude.springboot.app.security.service.JwtService;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(jwtConfig.getHeader());
        
        if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = header.replace(jwtConfig.getPrefix(), "");
        
        try {
            if (jwtService.validateToken(token)) {
                UsernamePasswordAuthenticationToken authentication = jwtService.getAuthentication(token);
                
                if (authentication != null && authentication.isAuthenticated()) {
                    // Validar si el usuario tiene una sesión activa
                    if (jwtService.isValidSessionToken(authentication.getName(), token)) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Sesión inválida o expirada");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido o expirado");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // No aplicar el filtro a rutas públicas
        return path.startsWith("/api/auth/") || 
               path.equals("/api/public/") || 
               path.contains("swagger") || 
               path.contains("api-docs");
    }
}

package com.claude.springboot.app.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.claude.springboot.app.security.config.JwtConfig;
import com.claude.springboot.app.security.dto.LoginRequest;
import com.claude.springboot.app.security.dto.LoginResponse;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.UsuarioRepository;
import com.claude.springboot.app.security.service.JwtService;
import com.claude.springboot.app.security.service.UserInfoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserInfoService userInfoService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(
            JwtConfig jwtConfig, 
            JwtService jwtService, 
            UserInfoService userInfoService) {
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
        this.userInfoService = userInfoService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        
        try {
            UserDetails userDetails = (UserDetails) authResult.getPrincipal();
            String token = jwtService.generateToken(authResult);
            
            // Actualizar el token de sesión
            jwtService.updateUserSessionToken(userDetails.getUsername(), token);

            // Construir la respuesta usando el servicio transaccional
            LoginResponse loginResponse = userInfoService.buildLoginResponse(
                userDetails.getUsername(),
                token,
                userDetails.getAuthorities().iterator().next().getAuthority()
            );

            response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), loginResponse);
            
        } catch (Exception e) {
            log.error("Error durante la autenticación exitosa", e);
            handleAuthenticationException(response, e);
        }
    }

    private void handleAuthenticationException(HttpServletResponse response, Exception e) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Error en el servidor");
        body.put("message", e.getMessage());

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), body);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword());

            return getAuthenticationManager().authenticate(authenticationToken);

        } catch (IOException e) {
            throw new RuntimeException("Error en la autenticación", e);
        }
    }

    

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, 
                                           HttpServletResponse response,
                                           AuthenticationException failed) throws IOException {
        Map<String, Object> body = new HashMap<>();
        
        // Obtener la causa raíz del error
        Throwable cause = failed.getCause() != null ? failed.getCause() : failed;
        String message = cause.getMessage();
        
        log.error("Error de autenticación. Tipo: {}, Mensaje: {}", cause.getClass().getName(), message);

        if (message != null && message.startsWith("CUENTA_NO_ACTIVADA:")) {
            String[] parts = message.split(":");
            body.put("error", "Cuenta no activada");
            body.put("message", parts[1]);
            body.put("tipo", "CUENTA_NO_ACTIVADA");
        } else if (message != null && message.startsWith("CUENTA_DESHABILITADA:")) {
            String[] parts = message.split(":");
            body.put("error", "Cuenta deshabilitada");
            body.put("message", parts[1]);
            body.put("tipo", "CUENTA_DESHABILITADA");
        } else if (failed instanceof BadCredentialsException) {
            body.put("error", "Credenciales inválidas");
            body.put("message", "Usuario o contraseña incorrectos");
            body.put("tipo", "CREDENCIALES_INVALIDAS");
        } else if (failed instanceof DisabledException) {
            body.put("error", "Cuenta deshabilitada");
            body.put("message", message != null ? message : "La cuenta está deshabilitada");
            body.put("tipo", "CUENTA_DESHABILITADA");
        } else {
            body.put("error", "Error de autenticación");
            body.put("message", message != null ? message : "Error al intentar autenticar");
            body.put("tipo", "ERROR_AUTENTICACION");
        }
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        
        log.debug("Respuesta de error enviada: {}", body);
    }
}
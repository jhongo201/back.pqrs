package com.claude.springboot.app.security.config;

import com.claude.springboot.app.security.filter.JwtAuthenticationFilter;
import com.claude.springboot.app.security.filter.JwtAuthorizationFilter;
import com.claude.springboot.app.security.repositories.UsuarioRepository;
import com.claude.springboot.app.security.service.CustomAuthenticationProvider;
import com.claude.springboot.app.security.service.JwtService;
import com.claude.springboot.app.security.service.RutaServiceImpl;
import com.claude.springboot.app.security.service.UserInfoService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final RutaServiceImpl rutaServiceImpl;
    private final CorsFilter corsFilter;
    private final UserInfoService userInfoService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtConfig, jwtService, userInfoService);
        jwtAuthenticationFilter.setAuthenticationManager(authManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");

        return http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/**").permitAll();
                    auth.requestMatchers("/api/pqrs/publico/**").permitAll();
                    auth.requestMatchers("/api/test/password/**").permitAll();
                    auth.requestMatchers("/error").permitAll();
                    auth.requestMatchers("/api/docs/**").authenticated(); // Agregamos esta línea
                    auth.requestMatchers("/api/usuarios/test-auth").permitAll(); // Permitimos el endpoint de prueba
                    // auth.requestMatchers("/api/usuarios/activar/{tokenOCodigo}").permitAll(); //
                    // Permitimos el endpoint de prueba
                    auth.requestMatchers("/api/files/**").permitAll(); // Permitir acceso a archivos
                    // Rutas públicas desde la base de datos
                    rutaServiceImpl.obtenerRutasPublicas()
                            .forEach(ruta -> auth.requestMatchers(ruta).permitAll());

                    // Permitir swagger si lo estás usando
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    // Todo lo demás requiere autenticación
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(new JwtAuthorizationFilter(jwtConfig, jwtService),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    AuthenticationManager authManager(HttpSecurity http, CustomAuthenticationProvider authProvider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authProvider)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Método para generar una contraseña de prueba
    public String generateEncodedPassword(String rawPassword) {
        return passwordEncoder().encode(rawPassword);
    }
}
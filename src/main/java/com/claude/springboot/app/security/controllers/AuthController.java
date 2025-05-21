package com.claude.springboot.app.security.controllers;


import com.claude.springboot.app.security.dto.LoginRequest;
import com.claude.springboot.app.security.dto.LoginResponse;
import com.claude.springboot.app.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);
        
        // Actualizar el token de sesión
        jwtService.updateUserSessionToken(loginRequest.getUsername(), token);
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        return ResponseEntity.ok(LoginResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .rol(userDetails.getAuthorities().iterator().next().getAuthority())
                .build());
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        jwtService.invalidateUserSession(username);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}

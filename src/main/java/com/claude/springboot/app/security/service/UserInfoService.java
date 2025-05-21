package com.claude.springboot.app.security.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.claude.springboot.app.security.dto.LoginResponse;
import com.claude.springboot.app.security.entities.Usuario;
import com.claude.springboot.app.security.repositories.UsuarioRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public LoginResponse buildLoginResponse(String username, String token, String authority) {
        log.debug("Construyendo respuesta de login para usuario: {}", username);
        
        Usuario usuario = usuarioRepository.findByUsernameWithRolAndPersona(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nombreCompleto = "";
        LoginResponse.AreaInfo areaInfo = null;
        LoginResponse.DireccionInfo direccionInfo = null;
        LoginResponse.EmpresaInfo empresaInfo = null;

        if (usuario.getPersona() != null) {
            nombreCompleto = usuario.getPersona().getNombres() + " " + usuario.getPersona().getApellidos();
            
            if (usuario.getPersona().getArea() != null) {
                areaInfo = LoginResponse.AreaInfo.builder()
                    .id(usuario.getPersona().getArea().getIdArea())
                    .nombre(usuario.getPersona().getArea().getNombre())
                    .build();
                
                if (usuario.getPersona().getArea().getDireccion() != null) {
                    direccionInfo = LoginResponse.DireccionInfo.builder()
                        .id(usuario.getPersona().getArea().getDireccion().getIdDireccion())
                        .nombre(usuario.getPersona().getArea().getDireccion().getNombre())
                        .build();
                }
                
                if (usuario.getPersona().getArea().getDireccion() != null && 
                    usuario.getPersona().getArea().getDireccion().getTerritorial() != null &&
                    usuario.getPersona().getArea().getDireccion().getTerritorial().getEmpresa() != null) {
                    
                    var empresa = usuario.getPersona().getArea().getDireccion().getTerritorial().getEmpresa();
                    empresaInfo = LoginResponse.EmpresaInfo.builder()
                        .id(empresa.getIdEmpresa())
                        .nombre(empresa.getNombre())
                        .build();
                } else if (usuario.getPersona().getEmpresa() != null) {
                    var empresa = usuario.getPersona().getEmpresa();
                    empresaInfo = LoginResponse.EmpresaInfo.builder()
                        .id(empresa.getIdEmpresa())
                        .nombre(empresa.getNombre())
                        .build();
                }
            }
        }

        return LoginResponse.builder()
                .message("Autenticación exitosa")
                .token(token)
                .username(username)
                .rol(authority.replace("ROLE_", ""))
                .nombreCompleto(nombreCompleto)
                .area(areaInfo)
                .direccion(direccionInfo)
                .empresa(empresaInfo)
                .build();
    }
}